package hackhub.app.Presentation.Controllers;

import hackhub.app.Application.DTOs.HackathonSummaryDTO;
import hackhub.app.Application.Requests.CreaHackathonRequest;
import hackhub.app.Application.Services.HackathonService;
import hackhub.app.Application.IUnitOfWork.IUnitOfWork;
import hackhub.app.Application.Utils.IJwtService;
import hackhub.app.Core.POJO_Entities.Hackathon;
import hackhub.app.Core.POJO_Entities.Partecipazione;
import hackhub.app.Core.POJO_Entities.User;
import hackhub.app.Presentation.Validators.HackathonValidator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller per la gestione degli Hackathon.
 */
@RestController
@RequestMapping("/api/hackathons")
public class HackathonController extends AbstractController {

  private static final Logger logger = LoggerFactory.getLogger(HackathonController.class);
  private final HackathonService hackathonService;
  private final HackathonValidator hackathonValidator;

  public HackathonController(
    HackathonService hackathonService,
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
    try {
      logger.debug("Searching for hackathon with ID: {}", hackathonId);
      HackathonSummaryDTO hackathon = hackathonService.getHackathonById(
        hackathonId
      );
      logger.debug("Hackathon found: {}", hackathon.getNome());
      return ResponseEntity.ok(hackathon);
    } catch (IllegalArgumentException e) {
      logger.warn("Hackathon not found: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    } catch (Exception e) {
      logger.error("Error occurred while fetching hackathon {}: {}", hackathonId, e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Crea un nuovo hackathon.
   *
   * @param token   Il token di autorizzazione dell'organizzatore.
   * @param request I dati per la creazione dell'hackathon.
   * @return L'hackathon creato o un errore di validazione.
   */
  @PostMapping("")
  public ResponseEntity<?> creaHackathon(
    @RequestHeader("Authorization") String token,
    @RequestBody CreaHackathonRequest request
  ) {
    User user = getAuthenticatedUser(token);
    validateRequest(hackathonValidator.validateCreation(request));
    Hackathon hackathon = hackathonService.creaHackathon(request, user.getId());
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
  public ResponseEntity<?> getClassifica(
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
  public ResponseEntity<List<hackhub.app.Core.POJO_Entities.Team>> getParticipants(
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
  public ResponseEntity<?> iscriviTeam(
    @PathVariable String hackathonId,
    @RequestParam String teamId,
    @RequestHeader(value = "Authorization", required = false) String token
  ) {
    try {
      User user = getAuthenticatedUser(token);

      // Forza il trattamento di hackathonId come String
      validateIds(hackathonId, teamId);

      Partecipazione partecipazione = hackathonService.iscriviTeamAHackathon(
        hackathonId,
        teamId,
        user.getId()
      );

      return ResponseEntity.ok(partecipazione);
    } catch (IllegalStateException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    } catch (SecurityException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("Errore durante l'iscrizione: " + e.getMessage());
    }
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
