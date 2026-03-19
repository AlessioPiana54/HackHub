package hackhub.app.Presentation.Controllers;

import hackhub.app.Application.Requests.CreaValutazioneRequest;
import hackhub.app.Application.Requests.InviaSottomissioneRequest;
import hackhub.app.Application.Requests.ModificaSottomissioneRequest;
import hackhub.app.Application.Services.SottomissioneService;
import hackhub.app.Application.IUnitOfWork.IUnitOfWork;
import hackhub.app.Application.Utils.IJwtService;
import hackhub.app.Core.POJO_Entities.Sottomissione;
import hackhub.app.Core.POJO_Entities.User;
import hackhub.app.Core.POJO_Entities.Valutazione;
import hackhub.app.Presentation.Validators.SottomissioneValidator;
import hackhub.app.Presentation.Validators.ValutazioneValidator;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller per la gestione delle sottomissioni.
 */
@RestController
@RequestMapping("/api/submissions")
public class SottomissioneController extends AbstractController {

  private final SottomissioneService sottomissioneService;
  private final SottomissioneValidator sottomissioneValidator;
  private final ValutazioneValidator valutazioneValidator;

  public SottomissioneController(
    SottomissioneService sottomissioneService,
    SottomissioneValidator sottomissioneValidator,
    ValutazioneValidator valutazioneValidator,
    IJwtService jwtService,
    IUnitOfWork unitOfWork
  ) {
    super(jwtService, unitOfWork);
    this.sottomissioneService = sottomissioneService;
    this.sottomissioneValidator = sottomissioneValidator;
    this.valutazioneValidator = valutazioneValidator;
  }

  /**
   * Invia una nuova sottomissione.
   *
   * @param token   Il token di autorizzazione dell'utente.
   * @param request I dati della sottomissione.
   * @return La sottomissione creata o un errore di validazione.
   */
  @PostMapping("")
  public ResponseEntity<?> inviaSottomissione(
    @RequestHeader("Authorization") String token,
    @RequestBody InviaSottomissioneRequest request
  ) {
    User user = getAuthenticatedUser(token);
    validateRequest(sottomissioneValidator.validateCreation(request));
    Sottomissione sottomissione = sottomissioneService.inviaSottomissione(
      request,
      user.getId()
    );
    return ResponseEntity.ok(sottomissione);
  }

  /**
   * Modifica una sottomissione esistente.
   *
   * @param id      L'ID della sottomissione da modificare.
   * @param token   Il token di autorizzazione dell'utente.
   * @param request I nuovi dati della sottomissione.
   * @return La sottomissione aggiornata.
   */
  @PatchMapping("/{id}")
  public ResponseEntity<?> modificaSottomissione(
    @PathVariable String id,
    @RequestHeader("Authorization") String token,
    @RequestBody ModificaSottomissioneRequest request
  ) {
    User user = getAuthenticatedUser(token);
    validateIds(id);
    validateRequest(sottomissioneValidator.validateModification(request));
    Sottomissione sottomissione = sottomissioneService.modificaSottomissione(
      request,
      user.getId(),
      id
    );
    return ResponseEntity.ok(sottomissione);
  }

  /**
   * Valuta una sottomissione.
   *
   * @param token   Il token di autorizzazione del giudice.
   * @param request I dati della valutazione.
   * @return La valutazione creata o un errore di validazione.
   */
  @PatchMapping("/{id}/evaluation")
  public ResponseEntity<?> valutaSottomissione(
    @RequestHeader("Authorization") String token,
    @PathVariable String id,
    @RequestBody CreaValutazioneRequest request
  ) {
    User user = getAuthenticatedUser(token);
    validateRequest(valutazioneValidator.validateCreation(request));
    Valutazione valutazione = sottomissioneService.valutaSottomissione(
      request,
      user.getId(),
      id
    );
    return ResponseEntity.ok(valutazione);
  }

  /**
   * Recupera le sottomissioni del team dell'utente.
   *
   * @param token Il token di autorizzazione dell'utente.
   * @return Lista di sottomissioni del team.
   */
  @GetMapping("/my-submissions")
  public ResponseEntity<List<Sottomissione>> getMySubmissions(
    @RequestHeader("Authorization") String token
  ) {
    User user = getAuthenticatedUser(token);
    List<Sottomissione> submissions = sottomissioneService.getTeamSubmissions(
      user.getId()
    );
    return ResponseEntity.ok(submissions);
  }

  /**
   * Recupera tutte le sottomissioni per un specifico Hackathon.
   *
   * @param idHackathon L'ID dell'Hackathon.
   * @return Lista di sottomissioni.
   */
  @GetMapping("/hackathon/{idHackathon}")
  public ResponseEntity<List<Sottomissione>> getSubmissionsByHackathon(
    @PathVariable String idHackathon,
    @RequestHeader("Authorization") String token
  ) {
    // Audit log or security check could be added here to ensure only Judge/Organizer access
    List<Sottomissione> submissions = sottomissioneService.getSubmissionsByHackathon(
      idHackathon
    );
    return ResponseEntity.ok(submissions);
  }
}
