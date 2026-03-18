package hackhub.app.Application.Services;

import static java.util.stream.Collectors.toList;

import hackhub.app.Application.DTOs.RichiestaSupportoDTO;
import hackhub.app.Application.IUnitOfWork.IUnitOfWork;
import hackhub.app.Application.Requests.CreaRichiestaSupportoRequest;
import hackhub.app.Application.Requests.ProponiCallRequest;
import hackhub.app.Application.Strategies.LinkStrategyContext;
import hackhub.app.Core.Enums.StatoHackathon;
import hackhub.app.Core.POJO_Entities.Hackathon;
import hackhub.app.Core.POJO_Entities.Partecipazione;
import hackhub.app.Core.POJO_Entities.RichiestaSupporto;
import hackhub.app.Core.POJO_Entities.Team;
import hackhub.app.Core.POJO_Entities.User;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Servizio per la gestione delle richieste di supporto.
 * Permette ai team di richiedere aiuto e ai mentori di gestire le richieste.
 */
@Service
@Transactional
public class RichiestaSupportoService extends AbstractService {

  private final LinkStrategyContext linkStrategyContext;

  public RichiestaSupportoService(
    IUnitOfWork unitOfWork,
    EntityFinder entityFinder,
    AuthorizationChecker authorizationChecker,
    LinkStrategyContext linkStrategyContext
  ) {
    super(unitOfWork, entityFinder, authorizationChecker);
    this.linkStrategyContext = linkStrategyContext;
  }

  /**
   * Crea una nuova richiesta di supporto per un team in un Hackathon.
   *
   * @param request       i dati della richiesta
   * @param richiedenteId l'ID dell'utente che crea la richiesta (deve essere
   *                      membro o leader)
   * @return la RichiestaSupporto creata
   * @throws IllegalArgumentException se partecipazione o utente non trovati
   * @throws SecurityException        se l'utente non fa parte del team
   */
  public void creaRichiestaSupporto(
    CreaRichiestaSupportoRequest request,
    String richiedenteId
  ) {
    // Verify ownership: richiedenteId is already validated by controller that extracted it from token

    Team team = findTeamOrThrow(request.getTeamId());
    Hackathon hackathon = findHackathonOrThrow(request.getHackathonId());

    // Validate that user is part of the team
    validateUserInTeam(
      team,
      richiedenteId,
      "L'utente non fa parte di questo team."
    );

    // Check if hackathon is in correct state
    if (hackathon.getStato() != StatoHackathon.IN_CORSO) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "Le richieste di supporto sono accettate solo durante l'hackathon."
      );
    }

    // Check if team is registered for this hackathon
    Partecipazione partecipazione = findPartecipazioneOrThrow(
      request.getTeamId(),
      request.getHackathonId()
    );

    // Create new support request
    User richiedente = findUserOrThrow(richiedenteId);
    RichiestaSupporto richiestaSupporto = new RichiestaSupporto(
      partecipazione,
      richiedente,
      request.getDescrizione()
    );

    // Validate and store the link using the strategy pattern (se presente)
    // linkStrategyContext.validate(
    //   request.getLinkProgetto(),
    //   java.util.List.of("GitHub")
    // );

    unitOfWork.richiestaSupportoRepository().save(richiestaSupporto);
  }

  /**
   * Restituisce le richieste di supporto assegnate agli hackathon di un mentore.
   *
   * @param hackathonId l'ID dell'Hackathon
   * @param mentorId    l'ID del mentore
   * @return la lista di RichiestaSupportoDTO
   * @throws IllegalArgumentException se l'hackathon non viene trovato
   * @throws SecurityException        se l'utente non è un mentore per l'hackathon
   */
  public List<RichiestaSupportoDTO> getRichiestePerMentore(
    String hackathonId,
    String mentorId
  ) {
    Hackathon hackathon = findHackathonOrThrow(hackathonId);

    validateUserIsMentorInHackathon(
      hackathon,
      mentorId,
      "L'utente non è un mentore per questo Hackathon"
    );

    // Recupera tutte le richieste di supporto per l'hackathon e le converte in DTO
    return unitOfWork
      .richiestaSupportoRepository()
      .findByPartecipazioneHackathonId(hackathonId)
      .stream()
      .map(this::mapToDTO)
      .collect(toList());
  }

  /**
   * Permette a un mentore di proporre una call per una richiesta di supporto.
   *
   * @param request  i dati per la call (link e data)
   * @param mentorId l'ID del mentore che propone la call
   * @return la RichiestaSupporto aggiornata
   * @throws IllegalArgumentException se il link non è valido o la richiesta non
   *                                  esiste
   * @throws SecurityException        se l'utente non è un mentore per l'hackathon
   */
  public RichiestaSupporto proponiCall(
    ProponiCallRequest request,
    String mentorId,
    String richiestaId
  ) {
    if (
      !linkStrategyContext.validate(
        request.getLinkCall(),
        List.of("Google Meet", "Webex")
      )
    ) {
      throw new IllegalArgumentException(
        "Il link della call deve essere di Google Meet o Webex."
      );
    }

    RichiestaSupporto richiesta = unitOfWork
      .richiestaSupportoRepository()
      .findById(richiestaId)
      .orElseThrow(() ->
        new IllegalArgumentException("Richiesta di supporto non trovata")
      );

    validateUserIsMentorInHackathon(
      richiesta.getHackathon(),
      mentorId,
      "L'utente non è un mentore per questo Hackathon e non può gestire la richiesta"
    );

    richiesta.setLinkCall(request.getLinkCall());
    richiesta.setDataCall(request.getDataCall());
    return unitOfWork.richiestaSupportoRepository().save(richiesta);
  }

  /**
   * Restituisce le richieste di supporto gestite (con call fissata) per un team.
   *
   * @param hackathonId l'ID dell'Hackathon
   * @param teamId      l'ID del Team
   * @param userId      l'ID dell'utente (membro del team)
   * @return la lista di richieste gestite come DTO
   * @throws IllegalArgumentException se team o partecipazione non trovati
   * @throws SecurityException        se l'utente non fa parte del team
   */
  public List<RichiestaSupportoDTO> getRichiesteGestitePerTeam(
    String hackathonId,
    String teamId,
    String userId
  ) {
    Team team = findTeamOrThrow(teamId);

    validateUserInTeam(
      team,
      userId,
      "L'utente non appartiene al team specificato."
    );

    findPartecipazioneOrThrow(teamId, hackathonId); // Assicura che il team partecipi

    // Filtra solo le richieste gestite (con data e link call) e le converte in DTO
    return unitOfWork
      .richiestaSupportoRepository()
      .findByPartecipazioneHackathonIdAndPartecipazioneTeamId(
        hackathonId,
        teamId
      )
      .stream()
      .filter(r ->
        r.getDataCall() != null &&
        r.getLinkCall() != null &&
        !r.getLinkCall().isEmpty()
      )
      .map(this::mapToDTO)
      .collect(toList());
  }

  // --- Private Helper Methods ---

  private RichiestaSupportoDTO mapToDTO(RichiestaSupporto r) {
    return new RichiestaSupportoDTO(
      r.getId(),
      r.getHackathon().getId(),
      r.getHackathon().getNome(),
      r.getTeam().getId(),
      r.getTeam().getNomeTeam(),
      r.getDescrizione(),
      r.getDataRichiesta(),
      r.getLinkCall(),
      r.getDataCall()
    );
  }
}
