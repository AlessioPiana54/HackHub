package hackhub.app.Application.Services;

import hackhub.app.Application.DTOs.TeamDTO;
import hackhub.app.Application.Exceptions.BusinessRuleException;
import hackhub.app.Application.Exceptions.UnauthorizedOperationException;
import hackhub.app.Application.IUnitOfWork.IUnitOfWork;
import hackhub.app.Application.Requests.CreaTeamRequest;
import hackhub.app.Application.Requests.UpdateTeamRequest;
import hackhub.app.Application.Services.Interfaces.ITeamService;
import hackhub.app.Core.Enums.Ruolo;
import hackhub.app.Core.Enums.StatoHackathon;
import hackhub.app.Core.POJO_Entities.Partecipazione;
import hackhub.app.Core.POJO_Entities.Team;
import hackhub.app.Core.POJO_Entities.User;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servizio per la gestione dei Team e delle relative operazioni.
 */
@Service
@Transactional
public class TeamService extends AbstractService implements ITeamService {

  private static final Logger logger = LoggerFactory.getLogger(TeamService.class);

  /** Stati in cui un team iscritto è considerato impegnato in un hackathon. */
  private static final Set<StatoHackathon> STATI_ATTIVI = EnumSet.of(
    StatoHackathon.IN_CORSO,
    StatoHackathon.IN_VALUTAZIONE,
    StatoHackathon.IN_PREMIAZIONE
  );

  private final TeamMapper teamMapper;

  public TeamService(
    IUnitOfWork unitOfWork,
    EntityFinder entityFinder,
    AuthorizationChecker authorizationChecker,
    TeamMapper teamMapper
  ) {
    super(unitOfWork, entityFinder, authorizationChecker);
    this.teamMapper = teamMapper;
  }

  /**
   * Elimina i team "orfani": team il cui leader risulta UTENTE_SENZA_TEAM
   * (stato incoerente lasciato da operazioni interrotte).
   */
  public void cleanupOrphanedTeams() {
    List<User> inconsistentUsers = unitOfWork
      .userRepository()
      .findAll()
      .stream()
      .filter(user -> user.getRuolo() == Ruolo.UTENTE_SENZA_TEAM)
      .filter(user -> unitOfWork.teamRepository().existsByLeaderId(user.getId()))
      .collect(Collectors.toList());

    for (User user : inconsistentUsers) {
      logger.warn("Utente {} risulta leader di un team orfano: pulizia in corso", user.getId());
      unitOfWork.teamRepository().deleteByLeaderId(user.getId());
    }

    logger.info("Pulizia team orfani completata: {} incoerenze corrette.", inconsistentUsers.size());
  }

  /**
   * Crea un nuovo Team e assegna l'utente richiedente come leader.
   *
   * @param request  i dati per la creazione del team (nome team)
   * @param leaderId l'ID dell'utente che crea il team
   * @return il Team creato
   * @throws BusinessRuleException          se il nome del team esiste già
   * @throws UnauthorizedOperationException se l'utente fa già parte di un team
   *                                        o ha un ruolo di staff
   */
  public TeamDTO creaTeam(CreaTeamRequest request, String leaderId) {
    User newLeader = findUserOrThrow(leaderId);

    if (unitOfWork.teamRepository().existsByNomeTeam(request.getNomeTeam())) {
      throw new BusinessRuleException("Esiste già un Team con questo nome.");
    }

    if (newLeader.getRuolo() != Ruolo.UTENTE_SENZA_TEAM) {
      throw new UnauthorizedOperationException(
        "L'utente specificato non ha i permessi necessari."
      );
    }

    // Un utente senza team non può risultare leader: se succede, il team è orfano
    if (unitOfWork.teamRepository().existsByLeaderId(leaderId)) {
      logger.warn("L'utente {} risulta già leader di un team orfano: lo elimino", leaderId);
      unitOfWork.teamRepository().deleteByLeaderId(leaderId);
    }

    newLeader.setRuolo(Ruolo.LEADER_TEAM);
    unitOfWork.userRepository().save(newLeader);

    Team nuovoTeam = new Team(request.getNomeTeam(), newLeader);
    unitOfWork.teamRepository().save(nuovoTeam);

    return teamMapper.toDTO(nuovoTeam);
  }

  /**
   * Aggiorna il nome di un team esistente (solo il leader).
   */
  public TeamDTO updateTeam(String teamId, UpdateTeamRequest request, String leaderId) {
    Team team = findTeamOrThrow(teamId);

    if (team.getLeaderSquadra() == null || !team.getLeaderSquadra().getId().equals(leaderId)) {
      throw new UnauthorizedOperationException(
        "Solo il Leader del Team può modificare i dettagli del team."
      );
    }

    String nuovoNome = request.getNomeTeam() != null ? request.getNomeTeam().trim() : "";
    if (!nuovoNome.isEmpty() && !team.getNomeTeam().equals(nuovoNome)) {
      if (nuovoNome.length() < 3 || nuovoNome.length() > 50) {
        throw new BusinessRuleException("Il nome del team deve avere tra i 3 e i 50 caratteri.");
      }
      if (unitOfWork.teamRepository().existsByNomeTeam(nuovoNome)) {
        throw new BusinessRuleException("Esiste già un Team con questo nome.");
      }
      team.setNomeTeam(nuovoNome);
    }

    unitOfWork.teamRepository().save(team);
    return teamMapper.toDTO(team);
  }

  /**
   * Permette a un membro di abbandonare il Team.
   * <ul>
   *   <li>un membro viene rimosso e torna UTENTE_SENZA_TEAM;</li>
   *   <li>il leader può uscire solo se è l'unico membro: se il team non ha mai
   *   partecipato a un hackathon viene eliminato, altrimenti viene sciolto
   *   (resta senza membri per conservare sottomissioni, classifiche e vincitori);</li>
   *   <li>il leader unico non può sciogliere il team durante un hackathon attivo.</li>
   * </ul>
   */
  public void abbandonaTeam(String teamId, String memberId) {
    Team team = findTeamOrThrow(teamId);
    User member = findUserOrThrow(memberId);

    validateUserInTeam(team, memberId, "L'utente non fa parte di questo Team.");

    boolean isLeader = team.getLeaderSquadra() != null
      && team.getLeaderSquadra().getId().equals(memberId);

    if (isLeader) {
      if (team.getMembri().size() > 1) {
        throw new BusinessRuleException(
          "Il Leader può abbandonare il team solo se è l'unico membro. Deve prima cedere il ruolo a un altro membro."
        );
      }
      chiudiTeam(team);
    } else {
      team.getMembri().removeIf(m -> m.getId().equals(memberId));
      unitOfWork.teamRepository().save(team);
    }

    member.setRuolo(Ruolo.UTENTE_SENZA_TEAM);
    unitOfWork.userRepository().save(member);
  }

  /**
   * Chiude il team del leader unico: eliminazione fisica se non ha storico,
   * scioglimento se ha partecipato ad almeno un hackathon.
   */
  private void chiudiTeam(Team team) {
    List<Partecipazione> partecipazioni = unitOfWork
      .partecipazioneRepository()
      .findByTeamId(team.getId());

    boolean impegnato = partecipazioni
      .stream()
      .anyMatch(p -> STATI_ATTIVI.contains(p.getHackathon().getStato()));
    if (impegnato) {
      throw new BusinessRuleException(
        "Impossibile abbandonare il team: è iscritto a un Hackathon in corso, in valutazione o in premiazione."
      );
    }

    // Gli inviti pendenti del team non hanno più senso in entrambi i casi
    unitOfWork.invitoRepository().findByTeam(team)
      .forEach(invito -> unitOfWork.invitoRepository().delete(invito));

    if (partecipazioni.isEmpty()) {
      unitOfWork.teamRepository().deleteById(team.getId());
    } else {
      team.getMembri().clear();
      team.setLeaderSquadra(null);
      unitOfWork.teamRepository().save(team);
    }
  }

  /**
   * Trasferisce il ruolo di Leader a un altro membro del team.
   */
  public TeamDTO trasferisciLeadership(String teamId, String newLeaderId, String currentLeaderId) {
    Team team = findTeamOrThrow(teamId);

    if (team.getLeaderSquadra() == null || !team.getLeaderSquadra().getId().equals(currentLeaderId)) {
      throw new UnauthorizedOperationException(
        "Solo l'attuale Leader può trasferire la leadership."
      );
    }

    User newLeader = findUserOrThrow(newLeaderId);
    validateUserInTeam(team, newLeaderId, "L'utente designato non fa parte di questo Team.");

    User currentLeader = team.getLeaderSquadra();
    currentLeader.setRuolo(Ruolo.MEMBRO_TEAM);
    unitOfWork.userRepository().save(currentLeader);

    newLeader.setRuolo(Ruolo.LEADER_TEAM);
    unitOfWork.userRepository().save(newLeader);

    team.setLeaderSquadra(newLeader);
    unitOfWork.teamRepository().save(team);

    return teamMapper.toDTO(team);
  }

  /**
   * Recupera i team di cui l'utente è membro.
   */
  public List<TeamDTO> getUserTeams(String userId) {
    return unitOfWork
      .teamRepository()
      .findByMembriId(userId)
      .stream()
      .map(teamMapper::toDTO)
      .collect(Collectors.toList());
  }

  /**
   * Recupera i dettagli di un team specifico.
   */
  public TeamDTO getTeamDetails(String teamId) {
    return teamMapper.toDTO(findTeamOrThrow(teamId));
  }
}
