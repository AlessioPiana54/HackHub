package hackhub.app.Presentation.Controllers;

import hackhub.app.Application.DTOs.ClassificaTeamDTO;
import hackhub.app.Application.DTOs.HackathonSummaryDTO;
import hackhub.app.Application.DTOs.MessageResponse;
import hackhub.app.Application.DTOs.TeamDTO;
import hackhub.app.Application.Requests.CreaHackathonRequest;
import hackhub.app.Application.Services.Interfaces.IHackathonService;
import hackhub.app.Application.IUnitOfWork.IUnitOfWork;
import hackhub.app.Application.Utils.IJwtService;
import hackhub.app.Core.POJO_Entities.User;
import hackhub.app.Presentation.Validators.HackathonValidator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller per la gestione degli Hackathon.
 */
@RestController
@RequestMapping("/api/hackathons")
public class HackathonController extends AbstractController {

  private static final Logger logger = LoggerFactory.getLogger(HackathonController.class);
  private final IHackathonService hackathonService;
  private final HackathonValidator hackathonValidator;

  public HackathonController(
    IHackathonService hackathonService,
    HackathonValidator hackathonValidator,
    IJwtService jwtService,
    IUnitOfWork unitOfWork
  ) {
    super(jwtService, unitOfWork);
    this.hackathonService = hackathonService;
    this.hackathonValidator = hackathonValidator;
  }


  /**
   * Recupera gli hackathon a cui è iscritto il team dell'utente loggato.
   *
   * @param token Il token di autorizzazione.
   * @return Lista di hackathon del team dell'utente.
   */
  @GetMapping("/my")
  public ResponseEntity<List<HackathonSummaryDTO>> getMyHackathons(
    @RequestHeader("Authorization") String token
  ) {
    User user = getAuthenticatedUser(token);
    return ResponseEntity.ok(hackathonService.getMyHackathons(user.getId()));
  }

  /**
   * Recupera gli hackathon assegnati al giudice loggato.
   *
   * @param token Il token di autorizzazione.
   * @return Lista di hackathon del giudice.
   */
  @GetMapping("/judge/my")
  public ResponseEntity<List<HackathonSummaryDTO>> getJudgeHackathons(
    @RequestHeader("Authorization") String token
  ) {
    User user = getAuthenticatedUser(token);
    return ResponseEntity.ok(hackathonService.getJudgeHackathons(user.getId()));
  }

  @GetMapping("/mentor/my")
  public ResponseEntity<List<HackathonSummaryDTO>> getMentorHackathons(
    @RequestHeader("Authorization") String token
  ) {
    User user = getAuthenticatedUser(token);
    return ResponseEntity.ok(hackathonService.getMentorHackathons(user.getId()));
  }

  /**
   * Recupera la lista degli hackathon pubblici.
   *
   * @return Una lista di Hackathon.
   */
  @GetMapping("")
  public ResponseEntity<List<HackathonSummaryDTO>> getHackathons() {
    return ResponseEntity.ok(hackathonService.getPublicHackathons());
  }

  /**
   * Recupera i dettagli di un hackathon specifico.
   *
   * @param hackathonId L'ID dell'hackathon da recuperare.
   * @return I dettagli dell'hackathon o un errore.
   */
  @GetMapping("/{hackathonId}")
  public ResponseEntity<HackathonSummaryDTO> getHackathonById(
    @PathVariable String hackathonId
  ) {
    logger.debug("Searching for hackathon with ID: {}", hackathonId);
    HackathonSummaryDTO hackathon = hackathonService.getHackathonById(hackathonId);
    logger.debug("Hackathon found: {}", hackathon.getNome());
    return ResponseEntity.ok(hackathon);
  }

  /**
   * Crea un nuovo hackathon.
   *
   * @param token   Il token di autorizzazione dell'organizzatore.
   * @param request I dati per la creazione dell'hackathon.
   * @return L'hackathon creato o un errore di validazione.
   */
  @PostMapping("")
  public ResponseEntity<HackathonSummaryDTO> creaHackathon(
    @RequestHeader("Authorization") String token,
    @RequestBody CreaHackathonRequest request
  ) {
    User user = getAuthenticatedUser(token);
    validateRequest(hackathonValidator.validateCreation(request));
    HackathonSummaryDTO hackathon = hackathonService.creaHackathon(request, user.getId());
    return ResponseEntity.ok(hackathon);
  }

  /**
   * Termina la fase di valutazione di un hackathon.
   *
   * @param hackathonId L'ID dell'hackathon.
   * @param token       Il token di autorizzazione.
   * @return Un messaggio di conferma.
   */
  @PatchMapping("/{hackathonId}/status")
  public ResponseEntity<String> terminaFaseValutazione(
    @PathVariable String hackathonId,
    @RequestHeader("Authorization") String token
  ) {
    User user = getAuthenticatedUser(token);
    validateIds(hackathonId);
    hackathonService.terminaFaseValutazione(hackathonId, user.getId());
    return ResponseEntity.ok(
      "Fase di valutazione terminata. Hackathon ora in fase di premiazione."
    );
  }

  /**
   * Recupera la classifica provvisoria o definitiva di un hackathon.
   *
   * @param hackathonId L'ID dell'hackathon.
   * @param token       Il token di autorizzazione.
   * @return La classifica dei team.
   */
  @GetMapping("/{hackathonId}/classifica")
  public ResponseEntity<List<ClassificaTeamDTO>> getClassifica(
    @PathVariable String hackathonId,
    @RequestHeader("Authorization") String token
  ) {
    User user = getAuthenticatedUser(token);
    validateIds(hackathonId);
    return ResponseEntity.ok(
      hackathonService.getClassifica(hackathonId, user.getId())
    );
  }

  /**
   * Recupera i team partecipanti a un hackathon.
   *
   * @param hackathonId L'ID dell'hackathon.
   * @return Lista di team partecipanti.
   */
  @GetMapping("/{hackathonId}/participants")
  public ResponseEntity<List<TeamDTO>> getParticipants(
    @PathVariable String hackathonId
  ) {
    validateIds(hackathonId);
    return ResponseEntity.ok(hackathonService.getParticipants(hackathonId));
  }

  /**
   * Iscrive un team a un hackathon.
   *
   * @param hackathonId L'ID dell'hackathon.
   * @param teamId     L'ID del team da iscrivere.
   * @param token       Il token di autorizzazione del leader del team.
   * @return La partecipazione creata o un errore.
   */
  @PostMapping("/{hackathonId}/join")
  public ResponseEntity<MessageResponse> iscriviTeam(
    @PathVariable String hackathonId,
    @RequestParam String teamId,
    @RequestHeader(value = "Authorization", required = false) String token
  ) {
    User user = getAuthenticatedUser(token);
    validateIds(hackathonId, teamId);
    hackathonService.iscriviTeamAHackathon(
      hackathonId,
      teamId,
      user.getId()
    );
    return ResponseEntity.ok(new MessageResponse("Team iscritto all'hackathon con successo."));
  }

  /**
   * Proclama il vincitore di un hackathon.
   *
   * @param hackathonId L'ID dell'hackathon.
   * @param teamId      L'ID del team vincitore.
   * @param token       Il token di autorizzazione.
   * @return Un messaggio di conferma.
   */
  @PostMapping("/{hackathonId}/winner")
  public ResponseEntity<String> proclamaVincitore(
    @PathVariable String hackathonId,
    @RequestParam String teamId,
    @RequestHeader("Authorization") String token
  ) {
    User user = getAuthenticatedUser(token);
    validateIds(hackathonId, teamId);
    hackathonService.proclamaVincitore(hackathonId, teamId, user.getId());
    return ResponseEntity.ok("Vincitore proclamato e Hackathon concluso.");
  }
}
