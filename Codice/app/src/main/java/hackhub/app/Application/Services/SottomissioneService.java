package hackhub.app.Application.Services;

import hackhub.app.Application.IUnitOfWork.IUnitOfWork;
import hackhub.app.Application.Requests.CreaValutazioneRequest;
import hackhub.app.Application.Requests.InviaSottomissioneRequest;
import hackhub.app.Application.Requests.ModificaSottomissioneRequest;
import hackhub.app.Application.Strategies.LinkStrategyContext;
import hackhub.app.Core.Enums.StatoHackathon;
import hackhub.app.Core.POJO_Entities.Hackathon;
import hackhub.app.Core.POJO_Entities.Partecipazione;
import hackhub.app.Core.POJO_Entities.Sottomissione;
import hackhub.app.Core.POJO_Entities.Team;
import hackhub.app.Core.POJO_Entities.User;
import hackhub.app.Core.POJO_Entities.Valutazione;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Servizio per la gestione delle sottomissioni dei progetti.
 * Gestisce l'invio, la modifica e la valutazione delle sottomissioni.
 */
@Service
@Transactional
public class SottomissioneService extends AbstractService {

  private final LinkStrategyContext linkStrategyContext;

  public SottomissioneService(
    IUnitOfWork unitOfWork,
    EntityFinder entityFinder,
    AuthorizationChecker authorizationChecker,
    LinkStrategyContext linkStrategyContext
  ) {
    super(unitOfWork, entityFinder, authorizationChecker);
    this.linkStrategyContext = linkStrategyContext;
  }

  /**
   * Invia una nuova sottomissione per un Hackathon.
   *
   * @param request i dati della sottomissione (codice, descrizione, video, ecc.)
   * @param userId  l'ID dell'utente che invia (deve essere membro o leader)
   * @param token   il token di autenticazione per verifica ownership
   * @return la Sottomissione creata
   * @throws IllegalArgumentException se hackathon, team non trovati o link github
   *                                  non valido
   * @throws IllegalStateException    se l'hackathon non è in corso o il team ha
   *                                  già sottomesso
   * @throws SecurityException        se l'utente non fa parte del team
   */
  public Sottomissione inviaSottomissione(
    InviaSottomissioneRequest request,
    String userId
  ) {
    // Verify ownership: userId is already validated by controller that extracted it from token

    Team team = findTeamOrThrow(request.getIdTeam());
    Hackathon hackathon = findHackathonOrThrow(request.getIdHackathon());

    // Validate that user is part of the team
    validateUserInTeam(
      team,
      userId,
      "Solo i membri del team possono inviare sottomissioni."
    );

    // Check if hackathon is in correct state for submissions
    if (hackathon.getStato() != StatoHackathon.IN_CORSO) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "Le sottomissioni sono accettate solo durante l'hackathon."
      );
    }

    // Check if team is registered for this hackathon
    Partecipazione partecipazione = findPartecipazioneOrThrow(
      request.getIdTeam(),
      request.getIdHackathon()
    );

    // Check if team has already submitted
    boolean esistente = unitOfWork
      .sottomissioneRepository()
      .existsByPartecipazione_Hackathon_IdAndPartecipazione_Team_Id(
        request.getIdHackathon(),
        request.getIdTeam()
      );
    if (esistente) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "Il team ha già inviato una sottomissione per questo hackathon."
      );
    }

    // Create new submission
    User mittente = findUserOrThrow(userId);
    Sottomissione sottomissione = new Sottomissione(
      partecipazione,
      mittente,
      request.getLinkProgetto(),
      request.getDescrizione()
    );

    // Validate and store the link using the strategy pattern
    linkStrategyContext.validate(
      request.getLinkProgetto(),
      java.util.List.of("GitHub")
    );

    return unitOfWork.sottomissioneRepository().save(sottomissione);
  }

  /**
   * Permette a un giudice di valutare una sottomissione.
   *
   * @param request   i dati della valutazione (voto, commento)
   * @param giudiceId l'ID del giudice
   * @param token     il token di autenticazione per verifica ownership
   * @return la Valutazione creata
   * @throws IllegalArgumentException se sottomissione o giudice non trovati
   * @throws SecurityException        se l'utente non è il giudice dell'hackathon
   * @throws IllegalStateException    se l'hackathon non è in fase di valutazione
   */
  public Valutazione valutaSottomissione(
    CreaValutazioneRequest request,
    String giudiceId,
    String sottomissioneId
  ) {
    // Verify ownership: giudiceId is already validated by controller that extracted it from token

    Sottomissione sottomissione = findSottomissioneOrThrow(sottomissioneId);
    Hackathon hackathon = sottomissione.getPartecipazione().getHackathon();

    if (hackathon.getStato() != StatoHackathon.IN_VALUTAZIONE) {
      throw new IllegalStateException(
        "L'Hackathon non è in fase di valutazione."
      );
    }

    if (!hackathon.getGiudice().getId().equals(giudiceId)) {
      throw new SecurityException(
        "Solo il giudice dell'Hackathon può valutare le sottomissioni."
      );
    }

    User giudice = findUserOrThrow(giudiceId);

    if (
      unitOfWork
        .valutazioneRepository()
        .existsBySottomissioneId(sottomissione.getId())
    ) {
      throw new IllegalArgumentException(
        "Questa sottomissione è già stata valutata."
      );
    }

    Valutazione valutazione = new Valutazione(
      giudice,
      request.getGiudizio(),
      request.getVoto()
    );
    valutazione.setSottomissione(sottomissione);

    unitOfWork.valutazioneRepository().save(valutazione);
    return valutazione;
  }

  /**
   * Permette di modificare una sottomissione esistente (se l'hackathon è ancora
   * in corso).
   *
   * @param request        i nuovi dati della sottomissione
   * @param userId         l'ID dell'utente che richiede la modifica
   * @param token          il token di autenticazione per verifica ownership
   * @param sottomissioneId l'ID della sottomissione da modificare
   * @return la Sottomissione modificata
   * @throws IllegalArgumentException se sottomissione non trovata o link github
   *                                  non valido
   * @throws IllegalStateException    se l'hackathon non è in corso
   * @throws SecurityException        se l'utente non fa parte del team
   */
  public Sottomissione modificaSottomissione(
    ModificaSottomissioneRequest request,
    String userId,
    String sottomissioneId
  ) {
    // Verify ownership: userId is already validated by controller that extracted it from token

    if (
      !linkStrategyContext.validate(
        request.getLinkProgetto(),
        java.util.List.of("GitHub")
      )
    ) {
      throw new IllegalArgumentException(
        "Link repository non valido o non supportato."
      );
    }

    Sottomissione sottomissione = findSottomissioneOrThrow(sottomissioneId);
    Team team = sottomissione.getPartecipazione().getTeam();
    Hackathon hackathon = sottomissione.getPartecipazione().getHackathon();

    if (hackathon.getStato() != StatoHackathon.IN_CORSO) {
      throw new IllegalStateException("Le sottomissioni sono chiuse.");
    }

    validateUserInTeam(
      team,
      userId,
      "L'utente non ha i permessi per modificare questa sottomissione."
    );

    sottomissione.setLinkProgetto(request.getLinkProgetto());
    sottomissione.setDescrizione(request.getDescrizione());

    unitOfWork.sottomissioneRepository().save(sottomissione);
    return sottomissione;
  }

  /**
   * Recupera le sottomissioni del team dell'utente.
   *
   * @param userId L'ID dell'utente.
   * @return Lista di sottomissioni del team.
   */
  public List<Sottomissione> getTeamSubmissions(String userId) {
    return unitOfWork
      .sottomissioneRepository()
      .findByPartecipazione_Team_Membri_Id(userId);
  }

  /**
   * Recupera tutte le sottomissioni per un specifico Hackathon.
   */
  public List<Sottomissione> getSubmissionsByHackathon(String hackathonId) {
    return unitOfWork
      .sottomissioneRepository()
      .findByPartecipazioneHackathonId(hackathonId);
  }
}
