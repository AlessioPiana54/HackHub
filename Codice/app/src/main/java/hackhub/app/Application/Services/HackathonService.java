package hackhub.app.Application.Services;

import hackhub.app.Application.DTOs.ClassificaTeamDTO;
import hackhub.app.Application.DTOs.HackathonSummaryDTO;
import hackhub.app.Application.DTOs.TeamDTO;
import hackhub.app.Application.Exceptions.BusinessRuleException;
import hackhub.app.Application.Exceptions.EntityNotFoundException;
import hackhub.app.Application.Exceptions.UnauthorizedOperationException;
import hackhub.app.Application.IUnitOfWork.IUnitOfWork;
import hackhub.app.Application.Requests.CreaHackathonRequest;
import hackhub.app.Application.Services.Interfaces.IHackathonService;
import hackhub.app.Application.Utils.IPaymentManager;
import hackhub.app.Core.Builders.HackathonBuilder;
import hackhub.app.Core.Enums.Ruolo;
import hackhub.app.Core.Enums.StatoHackathon;
import hackhub.app.Core.POJO_Entities.Hackathon;
import hackhub.app.Core.POJO_Entities.Partecipazione;
import hackhub.app.Core.POJO_Entities.Sottomissione;
import hackhub.app.Core.POJO_Entities.Team;
import hackhub.app.Core.POJO_Entities.User;
import hackhub.app.Core.POJO_Entities.Valutazione;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servizio per la gestione degli Hackathon.
 * Gestisce il ciclo di vita degli Hackathon (creazione, valutazione,
 * premiazione).
 */
@Service
@Transactional
public class HackathonService extends AbstractService implements IHackathonService {

  private final IPaymentManager paymentService;
  private final HackathonMapper hackathonMapper;
  private final TeamMapper teamMapper;

  public HackathonService(
    IUnitOfWork unitOfWork,
    EntityFinder entityFinder,
    AuthorizationChecker authorizationChecker,
    IPaymentManager paymentService,
    HackathonMapper hackathonMapper,
    TeamMapper teamMapper
  ) {
    super(unitOfWork, entityFinder, authorizationChecker);
    this.paymentService = paymentService;
    this.hackathonMapper = hackathonMapper;
    this.teamMapper = teamMapper;
  }

  /**
   * Crea un nuovo Hackathon.
   *
   * @param request         i dati per la creazione dell'hackathon
   * @param organizzatoreId l'ID dell'organizzatore che crea l'evento
   * @return l'Hackathon creato
   * @throws EntityNotFoundException         se organizzatore, giudice o mentori non esistono
   * @throws UnauthorizedOperationException  se gli utenti non hanno i ruoli corretti
   */
  public HackathonSummaryDTO creaHackathon(
    CreaHackathonRequest request,
    String organizzatoreId
  ) {
    User organizzatore = findUserOrThrow(organizzatoreId);
    User giudice = findUserOrThrow(request.getIdGiudice());

    validateUserRole(
      organizzatore,
      Ruolo.ORGANIZZATORE,
      "L'utente specificato come organizzatore non ha i permessi necessari."
    );
    validateUserRole(
      giudice,
      Ruolo.GIUDICE,
      "L'utente specificato come giudice non ha il ruolo di GIUDICE."
    );

    List<User> listaMentori = unitOfWork
      .userRepository()
      .findAllById(request.getIdMentori());
    if (listaMentori.size() != request.getIdMentori().size()) {
      throw new EntityNotFoundException("Uno o più mentori non trovati.");
    }
    for (User mentore : listaMentori) {
      validateUserRole(
        mentore,
        Ruolo.MENTORE,
        "L'utente " + mentore.getNome() + " non ha il ruolo di MENTORE."
      );
    }

    // Stato iniziale: se le iscrizioni sono già aperte l'hackathon parte IN_ISCRIZIONE
    StatoHackathon statoIniziale = request.getInizioIscrizioni().isAfter(LocalDateTime.now())
      ? StatoHackathon.IN_ATTESA
      : StatoHackathon.IN_ISCRIZIONE;

    Hackathon nuovoHackathon = new HackathonBuilder()
      .setNome(request.getNome())
      .setRegolamento(request.getRegolamento())
      .setPeriodoIscrizione(
        request.getInizioIscrizioni(),
        request.getScadenzaIscrizioni()
      )
      .setDurata(request.getDataInizio(), request.getDataFine())
      .setLuogo(request.getLuogo())
      .setLogoUrl(request.getLogoUrl())
      .setPremioInDenaro(request.getPremioInDenaro())
      .setOrganizzatore(organizzatore)
      .setGiudice(giudice)
      .setMentori(listaMentori)
      .setStato(statoIniziale)
      .build();

    unitOfWork.hackathonRepository().save(nuovoHackathon);

    return hackathonMapper.toSummaryDTO(nuovoHackathon);
  }

  /**
   * Termina la fase di valutazione di un Hackathon.
   *
   * @param hackathonId l'ID dell'Hackathon
   * @param giudiceId   l'ID del giudice che esegue l'operazione
   * @throws BusinessRuleException          se l'hackathon non è in valutazione o
   *                                        ci sono sottomissioni non valutate
   * @throws UnauthorizedOperationException se l'utente non è il giudice dell'hackathon
   */
  public void terminaFaseValutazione(String hackathonId, String giudiceId) {
    Hackathon hackathon = findHackathonOrThrow(hackathonId);

    if (hackathon.getStato() != StatoHackathon.IN_VALUTAZIONE) {
      throw new BusinessRuleException(
        "L'Hackathon non è in fase di valutazione"
      );
    }

    if (!hackathon.getGiudice().getId().equals(giudiceId)) {
      throw new UnauthorizedOperationException(
        "Solo il giudice dell'Hackathon può terminare la valutazione"
      );
    }

    Set<String> sottomissioniValutateIds = unitOfWork
      .valutazioneRepository()
      .findAllBySottomissione_Partecipazione_Hackathon_Id(hackathonId)
      .stream()
      .map(v -> v.getSottomissione().getId())
      .collect(Collectors.toSet());

    if (
      unitOfWork
        .sottomissioneRepository()
        .findByPartecipazioneHackathonId(hackathonId)
        .stream()
        .anyMatch(s -> !sottomissioniValutateIds.contains(s.getId()))
    ) {
      throw new BusinessRuleException(
        "Non tutte le sottomissioni sono state valutate. Impossibile terminare la fase di valutazione."
      );
    }

    hackathon.setStato(StatoHackathon.IN_PREMIAZIONE);
    unitOfWork.hackathonRepository().save(hackathon);
  }

  /**
   * Restituisce la classifica dei team per un dato Hackathon.
   *
   * @param hackathonId l'ID dell'Hackathon
   * @param requesterId l'ID dell'utente che richiede la classifica
   * @return la lista ordinata dei team con i relativi punteggi
   * @throws BusinessRuleException          se l'hackathon non è in premiazione o concluso
   * @throws UnauthorizedOperationException se il richiedente non ha i permessi necessari
   */
  public List<ClassificaTeamDTO> getClassifica(
    String hackathonId,
    String requesterId
  ) {
    Hackathon hackathon = findHackathonOrThrow(hackathonId);

    if (
      hackathon.getStato() != StatoHackathon.IN_PREMIAZIONE &&
      hackathon.getStato() != StatoHackathon.CONCLUSO
    ) {
      throw new BusinessRuleException(
        "La classifica è disponibile solo in fase di PREMIAZIONE o a Hackathon CONCLUSO."
      );
    }

    if (
      hackathon.getStato() == StatoHackathon.IN_PREMIAZIONE &&
      !hackathon.getOrganizzatore().getId().equals(requesterId) &&
      !hackathon.getGiudice().getId().equals(requesterId)
    ) {
      throw new UnauthorizedOperationException(
        "Solo l'organizzatore o il giudice dell'Hackathon possono visualizzare la classifica durante la fase di premiazione."
      );
    }

    List<Sottomissione> sottomissioni = unitOfWork
      .sottomissioneRepository()
      .findByPartecipazioneHackathonId(hackathonId);

    Map<String, Valutazione> mappaValutazioni = unitOfWork
      .valutazioneRepository()
      .findAllBySottomissione_Partecipazione_Hackathon_Id(hackathonId)
      .stream()
      .collect(Collectors.toMap(v -> v.getSottomissione().getId(), v -> v));

    return sottomissioni
      .stream()
      .map(s -> {
        Valutazione valutazione = mappaValutazioni.get(s.getId());
        if (valutazione == null) {
          throw new BusinessRuleException(
            "Trovata sottomissione senza valutazione in fase di premiazione: " +
            s.getId()
          );
        }
        return new ClassificaTeamDTO(
          s.getTeam().getId(),
          s.getTeam().getNomeTeam(),
          valutazione.getVoto()
        );
      })
      .sorted((t1, t2) -> Double.compare(t2.getPunteggio(), t1.getPunteggio()))
      .collect(Collectors.toList());
  }

  /**
   * Proclama un team vincitore dell'Hackathon ed effettua il pagamento del
   * premio al leader del team.
   *
   * @param hackathonId     l'ID dell'Hackathon
   * @param teamId          l'ID del Team vincitore
   * @param organizzatoreId l'ID dell'organizzatore
   * @throws BusinessRuleException          se l'hackathon non è in premiazione,
   *                                        il team non ha consegnato o il pagamento fallisce
   * @throws UnauthorizedOperationException se il richiedente non è l'organizzatore
   */
  public void proclamaVincitore(
    String hackathonId,
    String teamId,
    String organizzatoreId
  ) {
    Hackathon hackathon = findHackathonOrThrow(hackathonId);

    if (!hackathon.getOrganizzatore().getId().equals(organizzatoreId)) {
      throw new UnauthorizedOperationException(
        "Solo l'organizzatore può proclamare il vincitore."
      );
    }

    if (hackathon.getStato() != StatoHackathon.IN_PREMIAZIONE) {
      throw new BusinessRuleException(
        "L'Hackathon non è in fase di premiazione."
      );
    }

    Team team = findTeamOrThrow(teamId);

    boolean haSottomesso = unitOfWork
      .sottomissioneRepository()
      .existsByPartecipazione_Hackathon_IdAndPartecipazione_Team_Id(
        hackathonId,
        teamId
      );

    if (!haSottomesso) {
      throw new BusinessRuleException(
        "Il team selezionato non è iscritto o non ha effettuato sottomissioni per questo Hackathon."
      );
    }

    if (team.getLeaderSquadra() == null) {
      throw new BusinessRuleException(
        "Il team selezionato non ha un leader a cui versare il premio."
      );
    }

    try {
      paymentService.processPayment(
        team.getLeaderSquadra().getId(),
        hackathon.getPremioInDenaro()
      );
    } catch (Exception e) {
      throw new BusinessRuleException(
        "Errore durante il pagamento: " + e.getMessage()
      );
    }

    hackathon.setVincitore(team);
    hackathon.setStato(StatoHackathon.CONCLUSO);
    unitOfWork.hackathonRepository().save(hackathon);
  }

  /**
   * Iscrive un team a un hackathon.
   *
   * @param hackathonId ID dell'hackathon
   * @param teamId      ID del team
   * @param leaderId    ID del leader del team
   * @throws BusinessRuleException          se l'hackathon non è in fase di iscrizione
   *                                        o il team è già iscritto
   * @throws UnauthorizedOperationException se l'utente non è il leader del team
   */
  public void iscriviTeamAHackathon(
    String hackathonId,
    String teamId,
    String leaderId
  ) {
    Hackathon hackathon = findHackathonOrThrow(hackathonId);

    if (hackathon.getStato() != StatoHackathon.IN_ISCRIZIONE) {
      throw new BusinessRuleException(
        "L'hackathon non è aperto alle iscrizioni. Stato attuale: " +
        hackathon.getStato()
      );
    }

    Team team = findTeamOrThrow(teamId);

    if (
      team.getLeaderSquadra() == null ||
      !team.getLeaderSquadra().getId().equals(leaderId)
    ) {
      throw new UnauthorizedOperationException(
        "Solo il leader del team può iscrivere il team all'hackathon"
      );
    }

    Optional<Partecipazione> esistente = unitOfWork
      .partecipazioneRepository()
      .findByTeamIdAndHackathonId(teamId, hackathonId);

    if (esistente.isPresent()) {
      throw new BusinessRuleException(
        "Il team è già iscritto a questo hackathon"
      );
    }

    unitOfWork.partecipazioneRepository().save(new Partecipazione(team, hackathon));
  }

  /**
   * Restituisce una lista di riepilogo di tutti gli Hackathon.
   */
  public List<HackathonSummaryDTO> getPublicHackathons() {
    return toSummaries(unitOfWork.hackathonRepository().findAll());
  }

  /**
   * Restituisce gli hackathon a cui è iscritto il team dell'utente.
   * Un partecipante appartiene a un solo team alla volta.
   *
   * @param userId l'ID dell'utente
   */
  public List<HackathonSummaryDTO> getMyHackathons(String userId) {
    List<Team> teams = unitOfWork.teamRepository().findByMembriId(userId);

    if (teams == null || teams.isEmpty()) {
      return List.of();
    }

    String teamId = teams.get(0).getId();

    return unitOfWork
      .partecipazioneRepository()
      .findByTeamId(teamId)
      .stream()
      .map(p -> hackathonMapper.toSummaryDTO(p.getHackathon()))
      .collect(Collectors.toList());
  }

  /**
   * Restituisce gli hackathon assegnati a un giudice.
   */
  public List<HackathonSummaryDTO> getJudgeHackathons(String giudiceId) {
    return toSummaries(unitOfWork.hackathonRepository().findByGiudiceId(giudiceId));
  }

  /**
   * Restituisce gli hackathon assegnati a un mentore.
   */
  public List<HackathonSummaryDTO> getMentorHackathons(String mentoreId) {
    return toSummaries(unitOfWork.hackathonRepository().findByMentoriId(mentoreId));
  }

  /**
   * Restituisce gli hackathon creati da un organizzatore.
   */
  public List<HackathonSummaryDTO> getOrganizerHackathons(String organizzatoreId) {
    return toSummaries(
      unitOfWork.hackathonRepository().findByOrganizzatoreId(organizzatoreId)
    );
  }

  /**
   * Restituisce i team partecipanti a un hackathon.
   */
  public List<TeamDTO> getParticipants(String hackathonId) {
    return unitOfWork
      .partecipazioneRepository()
      .findByHackathonId(hackathonId)
      .stream()
      .map(p -> teamMapper.toDTO(p.getTeam()))
      .collect(Collectors.toList());
  }

  /**
   * Recupera i dettagli di un hackathon specifico per ID.
   */
  public HackathonSummaryDTO getHackathonById(String hackathonId) {
    return hackathonMapper.toSummaryDTO(findHackathonOrThrow(hackathonId));
  }

  private List<HackathonSummaryDTO> toSummaries(List<Hackathon> hackathons) {
    return hackathons
      .stream()
      .map(hackathonMapper::toSummaryDTO)
      .collect(Collectors.toList());
  }
}
