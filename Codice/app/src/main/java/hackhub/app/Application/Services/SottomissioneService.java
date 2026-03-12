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
import hackhub.app.Infrastructure.Utils.SecurityUtils;
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
    LinkStrategyContext linkStrategyContext
  ) {
    super(unitOfWork);
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
    String userId,
    String token
  ) {
    // Verify ownership: check that the current authenticated user matches the userId
    String currentUserId = SecurityUtils.getCurrentUserId(token);
    if (!currentUserId.equals(userId)) {
      throw new ResponseStatusException(
        HttpStatus.FORBIDDEN,
        "Non sei autorizzato a eseguire questa operazione per questo utente."
      );
    }

    if (
      !linkStrategyContext.validate(
        request.getLinkProgetto(),
        List.of("GitHub")
      )
    ) {
      throw new IllegalArgumentException(
        "Link repository non valido o non supportato."
      );
    }

    Partecipazione partecipazione = findPartecipazioneOrThrow(
      request.getIdTeam(),
      request.getIdHackathon()
    );
    Hackathon hackathon = partecipazione.getHackathon();
    Team team = partecipazione.getTeam();

    if (hackathon.getStato() != StatoHackathon.IN_CORSO) {
      throw new IllegalStateException(
        "L'Hackathon non è in corso. Impossibile inviare sottomissioni."
      );
    }

    validateUserInTeam(
      team,
      userId,
      "L'utente non fa parte del team specificato."
    );

    // Verifica che il team non abbia già inviato una sottomissione per questo
    // hackathon
    boolean exists = unitOfWork
      .sottomissioneRepository()
      .existsByPartecipazione_Hackathon_IdAndPartecipazione_Team_Id(
        request.getIdHackathon(),
        request.getIdTeam()
      );
    if (exists) {
      throw new IllegalStateException(
        "Il team ha già inviato una sottomissione per questo Hackathon."
      );
    }

    User mittente = findUserOrThrow(userId);

    Sottomissione sottomissione = new Sottomissione(
      partecipazione,
      mittente,
      request.getLinkProgetto(),
      request.getDescrizione()
    );

    unitOfWork.sottomissioneRepository().save(sottomissione);
    return sottomissione;
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
    String token
  ) {
    // Verify ownership: check that the current authenticated user matches the giudiceId
    String currentUserId = SecurityUtils.getCurrentUserId(token);
    if (!currentUserId.equals(giudiceId)) {
      throw new ResponseStatusException(
        HttpStatus.FORBIDDEN,
        "Non sei autorizzato a eseguire questa operazione per questo utente."
      );
    }

    Sottomissione sottomissione = findSottomissioneOrThrow(
      request.getIdSottomissione()
    );
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
   * @param request i nuovi dati della sottomissione
   * @param userId  l'ID dell'utente che richiede la modifica
   * @param token   il token di autenticazione per verifica ownership
   * @return la Sottomissione modificata
   * @throws IllegalArgumentException se sottomissione non trovata o link github
   *                                  non valido
   * @throws IllegalStateException    se l'hackathon non è in corso
   * @throws SecurityException        se l'utente non fa parte del team
   */
  public Sottomissione modificaSottomissione(
    ModificaSottomissioneRequest request,
    String userId,
    String token
  ) {
    // Verify ownership: check that the current authenticated user matches the userId
    String currentUserId = SecurityUtils.getCurrentUserId(token);
    if (!currentUserId.equals(userId)) {
      throw new ResponseStatusException(
        HttpStatus.FORBIDDEN,
        "Non sei autorizzato a eseguire questa operazione per questo utente."
      );
    }

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

    Sottomissione sottomissione = findSottomissioneOrThrow(
      request.getIdSottomissione()
    );
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
}
