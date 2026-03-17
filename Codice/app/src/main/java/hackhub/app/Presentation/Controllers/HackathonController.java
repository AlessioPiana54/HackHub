package hackhub.app.Presentation.Controllers;

import hackhub.app.Application.DTOs.HackathonSummaryDTO;
import hackhub.app.Application.Requests.CreaHackathonRequest;
import hackhub.app.Application.Services.HackathonService;
import hackhub.app.Application.Utils.ISessionManager;
import hackhub.app.Core.Enums.Ruolo;
import hackhub.app.Core.POJO_Entities.Hackathon;
import hackhub.app.Core.POJO_Entities.Partecipazione;
import hackhub.app.Core.POJO_Entities.User;
import hackhub.app.Presentation.Validators.HackathonValidator;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller per la gestione degli Hackathon.
 */
@RestController
@RequestMapping("/api/hackathons")
public class HackathonController extends AbstractController {

  private final HackathonService hackathonService;
  private final HackathonValidator hackathonValidator;

  public HackathonController(
    HackathonService hackathonService,
    HackathonValidator hackathonValidator,
    ISessionManager sessionManager
  ) {
    super(sessionManager);
    this.hackathonService = hackathonService;
    this.hackathonValidator = hackathonValidator;
  }

  /**
   * Endpoint temporaneo per creare sessioni di test
   */
  @GetMapping("/test-login")
  public ResponseEntity<String> createTestSession() {
    User testUser = new User();
    testUser.setId("15ca037b-570b-44d8-ab4d-060760e86a7c");
    testUser.setNome("Test");
    testUser.setEmail("test@example.com");
    testUser.setRuolo(Ruolo.LEADER_TEAM);

    String token = sessionManager.createSession(testUser);
    return ResponseEntity.ok("Token creato: " + token);
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
      System.out.println(
        "DEBUG: Searching for hackathon with ID: " + hackathonId
      );
      HackathonSummaryDTO hackathon = hackathonService.getHackathonById(
        hackathonId
      );
      System.out.println("DEBUG: Hackathon found: " + hackathon.getNome());
      return ResponseEntity.ok(hackathon);
    } catch (IllegalArgumentException e) {
      System.out.println("DEBUG: Hackathon not found: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    } catch (Exception e) {
      System.out.println("DEBUG: Error occurred: " + e.getMessage());
      e.printStackTrace();
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
      // Temporaneamente: saltiamo autenticazione per test
      User user = getAuthenticatedUser(token);
      if (user == null) {
        // Utente di test per debugging
        user = new User();
        user.setId("15ca037b-570b-44d8-ab4d-060760e86a7c");
        user.setNome("Test");
        user.setRuolo(Ruolo.LEADER_TEAM);
      }

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
