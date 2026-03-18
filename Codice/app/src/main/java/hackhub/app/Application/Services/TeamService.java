package hackhub.app.Application.Services;

import hackhub.app.Application.DTOs.TeamDTO;
import hackhub.app.Application.IUnitOfWork.IUnitOfWork;
import hackhub.app.Application.Requests.CreaTeamRequest;
import hackhub.app.Core.Enums.Ruolo;
import hackhub.app.Core.Enums.StatoHackathon;
import hackhub.app.Core.POJO_Entities.Hackathon;
import hackhub.app.Core.POJO_Entities.Partecipazione;
import hackhub.app.Core.POJO_Entities.Team;
import hackhub.app.Core.POJO_Entities.User;
import hackhub.app.Infrastructure.Utils.SecurityUtils;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Servizio per la gestione dei Team e delle relative operazioni.
 */
@Service
@Transactional
public class TeamService extends AbstractService {

  private static final Logger logger = LoggerFactory.getLogger(TeamService.class);

  public TeamService(IUnitOfWork unitOfWork) {
    super(unitOfWork);
  }

  /**
   * Crea un nuovo Team e assegna l'utente richiedente come leader.
   *
   * @param request  i dati per la creazione del team (nome team)
   * @param leaderId l'ID dell'utente che crea il team
   * @return il Team creato
   * @throws IllegalArgumentException se l'utente non viene trovato o il nome del
   *                                  team esiste già
   * @throws SecurityException        se l'utente non ha il ruolo appropriato
   */
  // **PREVENZIONE: Metodo per pulire team orfani**
  public void cleanupOrphanedTeams() {
    logger.info("TeamService.cleanupOrphanedTeams() - Starting cleanup...");

    // Trova tutti gli utenti che sono leader ma hanno ruolo UTENTE_SENZA_TEAM
    List<User> inconsistentUsers = unitOfWork
      .userRepository()
      .findAll()
      .stream()
      .filter(user -> user.getRuolo() == Ruolo.UTENTE_SENZA_TEAM)
      .filter(user -> unitOfWork.teamRepository().existsByLeaderId(user.getId())
      )
      .collect(java.util.stream.Collectors.toList());

    for (User user : inconsistentUsers) {
      logger.warn("Found inconsistent user {} - cleaning up orphaned team...", user.getId());
      unitOfWork.teamRepository().deleteByLeaderId(user.getId());
    }

    logger.info("TeamService.cleanupOrphanedTeams() - Cleanup completed. Fixed {} inconsistencies.", inconsistentUsers.size());
  }

  public Team creaTeam(CreaTeamRequest request, String leaderId) {
    User new_leader = findUserOrThrow(leaderId);

    if (unitOfWork.teamRepository().existsByNomeTeam(request.getNomeTeam())) {
      throw new IllegalArgumentException("Esiste già un Team con questo nome.");
    }

    if (new_leader.getRuolo() != Ruolo.UTENTE_SENZA_TEAM) {
      throw new SecurityException(
        "L'utente specificato non ha i permessi necessari."
      );
    }

    // **PREVENZIONE: Verifica che l'utente non sia già leader di un team orfano**
    if (unitOfWork.teamRepository().existsByLeaderId(leaderId)) {
      logger.warn("User {} is already a leader. Cleaning up orphaned team...", leaderId);
      // Rimuovi il team orfano prima di crearne uno nuovo
      unitOfWork.teamRepository().deleteByLeaderId(leaderId);
    }

    new_leader.setRuolo(Ruolo.LEADER_TEAM);
    unitOfWork.userRepository().save(new_leader);

    Team nuovoTeam = new Team(request.getNomeTeam(), new_leader);
    unitOfWork.teamRepository().save(nuovoTeam);

    return nuovoTeam;
  }

  /**
   * Aggiorna i dati di un team esistente.
   *
   * @param teamId   l'ID del team da aggiornare
   * @param request  i nuovi dati del team
   * @param leaderId l'ID dell'utente che richiede la modifica (deve essere il leader)
   * @return il Team aggiornato
   */
  public Team updateTeam(String teamId, hackhub.app.Application.Requests.UpdateTeamRequest request, String leaderId) {
    Team team = findTeamOrThrow(teamId);

    // Verifica che l'utente che fa la richiesta sia il leader del team
    if (team.getLeaderSquadra() == null || !team.getLeaderSquadra().getId().equals(leaderId)) {
      throw new SecurityException("Solo il Leader del Team può modificare i dettagli del team.");
    }

    // Se il nome è stato modificato, verifica che non esista già
    if (request.getNomeTeam() != null && !request.getNomeTeam().trim().isEmpty() && !team.getNomeTeam().equals(request.getNomeTeam())) {
      if (unitOfWork.teamRepository().existsByNomeTeam(request.getNomeTeam())) {
        throw new IllegalArgumentException("Esiste già un Team con questo nome.");
      }
      team.setNomeTeam(request.getNomeTeam().trim()); // Assuming setNomeTeam is added manually since we didn't add it in the previous step
    }

    unitOfWork.teamRepository().save(team);
    return team;
  }

  /**
   * Iscrive un Team ad un Hackathon.
   *
   * @param teamId        l'ID del Team da iscrivere
   * @param hackathonId   l'ID dell'Hackathon a cui iscriversi
   * @param richiedenteId l'ID dell'utente che richiede l'iscrizione (deve essere
   *                      il leader)
   * @param token         il token di autenticazione per verifica ownership
   * @return l'oggetto Partecipazione creato
   * @throws IllegalArgumentException se team o hackathon non trovati, iscrizioni
   *                                  chiuse o team già iscritto
   * @throws SecurityException        se il richiedente non è il leader del team
   */
  public Partecipazione iscriviTeam(
    String teamId,
    String hackathonId,
    String richiedenteId,
    String token
  ) {
    // Verify ownership: check that the current authenticated user matches the richiedenteId
    String currentUserId = SecurityUtils.getCurrentUserId(token);
    if (!currentUserId.equals(richiedenteId)) {
      throw new ResponseStatusException(
        HttpStatus.FORBIDDEN,
        "Non sei autorizzato a eseguire questa operazione per questo utente."
      );
    }

    Team team = findTeamOrThrow(teamId);

    if (
      team.getLeaderSquadra() == null ||
      !team.getLeaderSquadra().getId().equals(richiedenteId)
    ) {
      throw new SecurityException(
        "Solo il Leader del Team può effettuare l'iscrizione."
      );
    }

    Hackathon hackathon = findHackathonOrThrow(hackathonId);

    if (hackathon.getStato() != StatoHackathon.IN_ISCRIZIONE) {
      throw new IllegalArgumentException(
        "Le iscrizioni per questo Hackathon non sono aperte."
      );
    }

    // Verifica se il team è già impegnato in altre competizioni non ancora concluse
    List<Partecipazione> partecipazioni = unitOfWork
      .partecipazioneRepository()
      .findByTeamId(teamId);
    for (Partecipazione p : partecipazioni) {
      if (p.getHackathon().getStato() != StatoHackathon.CONCLUSO) {
        throw new IllegalArgumentException(
          "Il Team è già iscritto ad un Hackathon attivo."
        );
      }
    }

    Partecipazione p = new Partecipazione(team, hackathon);
    unitOfWork.partecipazioneRepository().save(p);
    return p;
  }

  /**
   * Permette ad un membro di abbandonare il Team.
   *
   * @param teamId   l'ID del Team da abbandonare
   * @param memberId l'ID del membro che vuole lasciare il team
   * @throws IllegalArgumentException se team o utente non trovati, o se l'utente
   *                                  non fa parte del team
   */
  public void abbandonaTeam(String teamId, String memberId) {
    logger.debug("TeamService.abbandonaTeam() called for teamId: {}, memberId: {}", teamId, memberId);

    Team team = findTeamOrThrow(teamId);
    User member = findUserOrThrow(memberId);

    logger.debug("Team found: {}", team.getNomeTeam());
    logger.debug("Team leader ID: {}", team.getLeaderSquadra().getId());

    // Check if the user is part of the team (throws SecurityException if not)
    validateUserInTeam(team, memberId, "L'utente non fa parte di questo Team.");

    // Check if the user is the leader
    if (team.getLeaderSquadra().getId().equals(memberId)) {
      logger.debug("User is team leader, checking if can abandon...");

      // Leader can abandon only if they are the only member
      if (team.getMembri().size() > 1) {
        logger.error("Leader cannot abandon team with multiple members!");
        throw new IllegalArgumentException(
          "Il Leader può abbandonare il team solo se è l'unico membro. Deve prima cedere il ruolo a un altro membro."
        );
      }

      logger.debug("Leader is the only member, can abandon team...");
    }

    logger.debug("User is not leader, proceeding with abandonment...");

    // If user is the leader and is the only member, delete the entire team
    if (
      team.getLeaderSquadra().getId().equals(memberId) &&
      team.getMembri().size() == 1
    ) {
      logger.info("Leader is the only member, deleting entire team...");
      // Delete the team completely to avoid constraint violations
      unitOfWork.teamRepository().deleteById(teamId);
    } else {
      // Remove member from team
      team.getMembri().removeIf(m -> m.getId().equals(memberId));
      unitOfWork.teamRepository().save(team);
    }

    // Update user role
    member.setRuolo(Ruolo.UTENTE_SENZA_TEAM);
    unitOfWork.userRepository().save(member);
  }

  /**
   * Trasferisce il ruolo di Leader a un altro membro del team.
   *
   * @param teamId          L'ID del Team.
   * @param newLeaderId     L'ID dell'utente a cui cedere la leadership.
   * @param currentLeaderId L'ID dell'attuale leader.
   */
  public Team trasferisciLeadership(String teamId, String newLeaderId, String currentLeaderId) {
    Team team = findTeamOrThrow(teamId);

    // Verifica che chi fa la richiesta sia l'attuale leader
    if (team.getLeaderSquadra() == null || !team.getLeaderSquadra().getId().equals(currentLeaderId)) {
      throw new SecurityException("Solo l'attuale Leader può trasferire la leadership.");
    }

    // Verifica che il nuovo leader faccia parte del team
    User newLeader = findUserOrThrow(newLeaderId);
    validateUserInTeam(team, newLeaderId, "L'utente designato non fa parte di questo Team.");

    // Aggiorna il ruolo del vecchio leader a MEMBRO_TEAM
    User currentLeader = team.getLeaderSquadra();
    currentLeader.setRuolo(Ruolo.MEMBRO_TEAM);
    unitOfWork.userRepository().save(currentLeader);

    // Aggiorna il ruolo del nuovo leader a LEADER_TEAM
    newLeader.setRuolo(Ruolo.LEADER_TEAM);
    unitOfWork.userRepository().save(newLeader);

    // Aggiorna il team
    team.setLeaderSquadra(newLeader);
    unitOfWork.teamRepository().save(team);

    return team;
  }

  /**
   * Recupera i team di cui l'utente è membro.
   *
   * @param userId L'ID dell'utente.
   * @return Lista di TeamDTO.
   */
  public List<TeamDTO> getUserTeams(String userId) {
    // Usa l'apposito metodo repository per far eseguire il JOIN al database
    List<Team> teams = unitOfWork.teamRepository().findByMembriId(userId);

    return teams
      .stream()
      .map(this::convertToTeamDTO)
      .collect(Collectors.toList());
  }

  /**
   * Recupera i dettagli di un team specifico.
   *
   * @param teamId L'ID del team.
   * @return TeamDTO con dettagli completi.
   */
  public TeamDTO getTeamDetails(String teamId) {
    Team team = findTeamOrThrow(teamId);
    return convertToTeamDTO(team);
  }

  /**
   * Converte un'entità Team in TeamDTO.
   *
   * @param team L'entità Team.
   * @return TeamDTO.
   */
  private TeamDTO convertToTeamDTO(Team team) {
    List<String> membriIds = team
      .getMembri()
      .stream()
      .map(User::getId)
      .collect(Collectors.toList());

    List<String> membriNomi = team
      .getMembri()
      .stream()
      .map(m -> m.getNome() + " " + m.getCognome())
      .collect(Collectors.toList());

    return new TeamDTO(
      team.getId(),
      team.getNomeTeam(),
      team.getLeaderSquadra().getId(),
      team.getLeaderSquadra().getNome() +
      " " +
      team.getLeaderSquadra().getCognome(),
      membriIds,
      membriNomi,
      team.getDataCreazione()
    );
  }
}
