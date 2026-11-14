package hackhub.app.Application.Services;

import hackhub.app.Application.DTOs.SottomissioneDTO;
import hackhub.app.Application.DTOs.ValutazioneDTO;
import hackhub.app.Application.Exceptions.BusinessRuleException;
import hackhub.app.Application.Exceptions.UnauthorizedOperationException;
import hackhub.app.Application.IUnitOfWork.IUnitOfWork;
import hackhub.app.Application.Requests.CreaValutazioneRequest;
import hackhub.app.Application.Requests.InviaSottomissioneRequest;
import hackhub.app.Application.Requests.ModificaSottomissioneRequest;
import hackhub.app.Application.Services.Interfaces.ISottomissioneService;
import hackhub.app.Application.Strategies.LinkStrategyContext;
import hackhub.app.Core.Enums.StatoHackathon;
import hackhub.app.Core.POJO_Entities.Hackathon;
import hackhub.app.Core.POJO_Entities.Partecipazione;
import hackhub.app.Core.POJO_Entities.Sottomissione;
import hackhub.app.Core.POJO_Entities.Team;
import hackhub.app.Core.POJO_Entities.User;
import hackhub.app.Core.POJO_Entities.Valutazione;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servizio per la gestione delle sottomissioni dei progetti.
 * Gestisce l'invio, la modifica e la valutazione delle sottomissioni.
 */
@Service
@Transactional
public class SottomissioneService extends AbstractService implements ISottomissioneService {

  private static final List<String> PIATTAFORME_REPOSITORY = List.of("GitHub");
  private static final String ERRORE_LINK =
    "Il link del progetto deve essere un repository GitHub (https://github.com/...).";

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
   * @param request i dati della sottomissione (team, hackathon, link, descrizione)
   * @param userId  l'ID dell'utente che invia (deve essere membro o leader)
   * @return la Sottomissione creata
   * @throws UnauthorizedOperationException se l'utente non fa parte del team
   * @throws BusinessRuleException          se l'hackathon non è in corso, il team ha
   *                                        già consegnato o il link non è GitHub
   */
  public SottomissioneDTO inviaSottomissione(
    InviaSottomissioneRequest request,
    String userId
  ) {
    Team team = findTeamOrThrow(request.getIdTeam());
    Hackathon hackathon = findHackathonOrThrow(request.getIdHackathon());

    validateUserInTeam(
      team,
      userId,
      "Solo i membri del team possono inviare sottomissioni."
    );

    if (hackathon.getStato() != StatoHackathon.IN_CORSO) {
      throw new BusinessRuleException(
        "Le sottomissioni sono accettate solo durante l'hackathon."
      );
    }

    Partecipazione partecipazione = findPartecipazioneOrThrow(
      request.getIdTeam(),
      request.getIdHackathon()
    );

    boolean esistente = unitOfWork
      .sottomissioneRepository()
      .existsByPartecipazione_Hackathon_IdAndPartecipazione_Team_Id(
        request.getIdHackathon(),
        request.getIdTeam()
      );
    if (esistente) {
      throw new BusinessRuleException(
        "Il team ha già inviato una sottomissione per questo hackathon."
      );
    }

    validaLinkRepository(request.getLinkProgetto());

    User mittente = findUserOrThrow(userId);
    Sottomissione sottomissione = new Sottomissione(
      partecipazione,
      mittente,
      request.getLinkProgetto().trim(),
      request.getDescrizione()
    );

    Sottomissione saved = unitOfWork.sottomissioneRepository().save(sottomissione);
    return convertToDTO(saved, null);
  }

  /**
   * Permette al giudice dell'hackathon di valutare una sottomissione (una sola volta).
   *
   * @param request         i dati della valutazione (voto, giudizio)
   * @param giudiceId       l'ID del giudice
   * @param sottomissioneId l'ID della sottomissione
   * @return la valutazione creata
   * @throws BusinessRuleException          se l'hackathon non è in valutazione o la
   *                                        sottomissione è già stata valutata
   * @throws UnauthorizedOperationException se l'utente non è il giudice dell'hackathon
   */
  public ValutazioneDTO valutaSottomissione(
    CreaValutazioneRequest request,
    String giudiceId,
    String sottomissioneId
  ) {
    Sottomissione sottomissione = findSottomissioneOrThrow(sottomissioneId);
    Hackathon hackathon = sottomissione.getPartecipazione().getHackathon();

    if (hackathon.getStato() != StatoHackathon.IN_VALUTAZIONE) {
      throw new BusinessRuleException(
        "L'Hackathon non è in fase di valutazione."
      );
    }

    if (!hackathon.getGiudice().getId().equals(giudiceId)) {
      throw new UnauthorizedOperationException(
        "Solo il giudice dell'Hackathon può valutare le sottomissioni."
      );
    }

    User giudice = findUserOrThrow(giudiceId);

    if (
      unitOfWork
        .valutazioneRepository()
        .existsBySottomissioneId(sottomissione.getId())
    ) {
      throw new BusinessRuleException(
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
    return toValutazioneDTO(valutazione);
  }

  /**
   * Modifica una sottomissione esistente finché l'hackathon è in corso.
   *
   * @throws BusinessRuleException          se il link non è GitHub o l'hackathon non è in corso
   * @throws UnauthorizedOperationException se l'utente non fa parte del team
   */
  public SottomissioneDTO modificaSottomissione(
    ModificaSottomissioneRequest request,
    String userId,
    String sottomissioneId
  ) {
    validaLinkRepository(request.getLinkProgetto());

    Sottomissione sottomissione = findSottomissioneOrThrow(sottomissioneId);
    Team team = sottomissione.getPartecipazione().getTeam();
    Hackathon hackathon = sottomissione.getPartecipazione().getHackathon();

    if (hackathon.getStato() != StatoHackathon.IN_CORSO) {
      throw new BusinessRuleException("Le sottomissioni sono chiuse.");
    }

    validateUserInTeam(
      team,
      userId,
      "L'utente non ha i permessi per modificare questa sottomissione."
    );

    sottomissione.setLinkProgetto(request.getLinkProgetto().trim());
    sottomissione.setDescrizione(request.getDescrizione());

    Sottomissione saved = unitOfWork.sottomissioneRepository().save(sottomissione);
    return convertToDTO(saved, null);
  }

  /**
   * Recupera le sottomissioni dei team di cui l'utente fa parte, con l'eventuale
   * valutazione ricevuta.
   */
  public List<SottomissioneDTO> getTeamSubmissions(String userId) {
    return unitOfWork
      .sottomissioneRepository()
      .findByPartecipazione_Team_Membri_Id(userId)
      .stream()
      .map(s -> convertToDTO(
        s,
        unitOfWork.valutazioneRepository().findBySottomissioneId(s.getId()).orElse(null)
      ))
      .collect(Collectors.toList());
  }

  /**
   * Recupera tutte le sottomissioni di un Hackathon, con le relative valutazioni.
   * Consentito solo a organizzatore, giudice e mentori dell'hackathon.
   *
   * @throws UnauthorizedOperationException se il richiedente non è dello staff dell'hackathon
   */
  public List<SottomissioneDTO> getSubmissionsByHackathon(String hackathonId, String requesterId) {
    Hackathon hackathon = findHackathonOrThrow(hackathonId);

    boolean isOrganizzatore = hackathon.getOrganizzatore() != null
      && hackathon.getOrganizzatore().getId().equals(requesterId);
    boolean isGiudice = hackathon.getGiudice() != null
      && hackathon.getGiudice().getId().equals(requesterId);
    boolean isMentore = hackathon.getMentori() != null
      && hackathon.getMentori().stream().anyMatch(m -> m.getId().equals(requesterId));

    if (!isOrganizzatore && !isGiudice && !isMentore) {
      throw new UnauthorizedOperationException(
        "Solo organizzatore, giudice e mentori dell'hackathon possono vedere tutte le sottomissioni."
      );
    }

    Map<String, Valutazione> valutazioni = unitOfWork
      .valutazioneRepository()
      .findAllBySottomissione_Partecipazione_Hackathon_Id(hackathonId)
      .stream()
      .collect(Collectors.toMap(v -> v.getSottomissione().getId(), Function.identity()));

    return unitOfWork
      .sottomissioneRepository()
      .findByPartecipazioneHackathonId(hackathonId)
      .stream()
      .map(s -> convertToDTO(s, valutazioni.get(s.getId())))
      .collect(Collectors.toList());
  }

  // --- Private Helper Methods ---

  private void validaLinkRepository(String link) {
    if (!linkStrategyContext.validate(link, PIATTAFORME_REPOSITORY)) {
      throw new BusinessRuleException(ERRORE_LINK);
    }
  }

  private SottomissioneDTO convertToDTO(Sottomissione s, Valutazione v) {
    return new SottomissioneDTO(
      s.getId(),
      s.getHackathon().getId(),
      s.getHackathon().getNome(),
      s.getTeam().getId(),
      s.getTeam().getNomeTeam(),
      s.getMittente().getId(),
      s.getMittente().getNome() + " " + s.getMittente().getCognome(),
      s.getLinkProgetto(),
      s.getDescrizione(),
      s.getDataSottomissione(),
      v != null ? v.getVoto() : null,
      v != null ? v.getGiudizio() : null
    );
  }

  private ValutazioneDTO toValutazioneDTO(Valutazione v) {
    Sottomissione s = v.getSottomissione();
    return new ValutazioneDTO(
      v.getId(),
      s.getId(),
      s.getTeam().getId(),
      s.getTeam().getNomeTeam(),
      v.getGiudice().getId(),
      v.getGiudice().getNome() + " " + v.getGiudice().getCognome(),
      v.getVoto(),
      v.getGiudizio()
    );
  }
}
