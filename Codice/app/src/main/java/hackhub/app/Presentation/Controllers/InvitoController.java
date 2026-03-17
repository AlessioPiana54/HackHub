package hackhub.app.Presentation.Controllers;

import hackhub.app.Application.DTOs.InvitoDTO;
import hackhub.app.Application.Requests.CreaInvitoRequest;
import hackhub.app.Application.Requests.RispostaInvitoRequest;
import hackhub.app.Application.Services.InvitoService;
import hackhub.app.Application.Utils.ISessionManager;
import hackhub.app.Core.POJO_Entities.Invito;
import hackhub.app.Core.POJO_Entities.User;
import hackhub.app.Presentation.Validators.InvitoValidator;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller per la gestione degli inviti.
 */
@RestController
@RequestMapping("/api/invitations")
public class InvitoController extends AbstractController {

  private final InvitoService invitoService;
  private final InvitoValidator invitoValidator;

  public InvitoController(
    InvitoService invitoService,
    InvitoValidator invitoValidator,
    ISessionManager sessionManager
  ) {
    super(sessionManager);
    this.invitoService = invitoService;
    this.invitoValidator = invitoValidator;
  }

  /**
   * Invia un nuovo invito.
   *
   * @param token   Il token di autorizzazione del mittente.
   * @param request I dati per invitare un utente.
   * @return L'invito creato o un errore di validazione.
   */
  @PostMapping("")
  public ResponseEntity<?> inviaInvito(
    @RequestHeader("Authorization") String token,
    @RequestBody CreaInvitoRequest request
  ) {
    User user = getAuthenticatedUser(token);
    validateRequest(invitoValidator.validateCreation(request));
    Invito invito = invitoService.inviaInvito(request, user.getId());
    return ResponseEntity.ok(invito);
  }

  /**
   * Risponde a un invito ricevuto.
   *
   * @param token   Il token di autorizzazione dell'utente invitato.
   * @param request I dati relativi alla risposta (accettazione o rifiuto).
   * @return Un messaggio di conferma.
   */
  @PatchMapping("/{id}")
  public ResponseEntity<?> rispondiInvito(
    @RequestHeader("Authorization") String token,
    @PathVariable String id,
    @RequestBody RispostaInvitoRequest request
  ) {
    User user = getAuthenticatedUser(token);
    validateRequest(invitoValidator.validateRisposta(request));
    invitoService.gestisciRisposta(request, user.getId(), id);
    return ResponseEntity.ok(new hackhub.app.Application.DTOs.MessageResponse("Risposta registrata con successo."));
  }

  /**
   * Recupera gli inviti ricevuti dall'utente.
   *
   * @param token Il token di autorizzazione dell'utente.
   * @return Lista di InvitoDTO ricevuti.
   */
  @GetMapping("/received")
  public ResponseEntity<List<InvitoDTO>> getReceivedInvitations(
    @RequestHeader("Authorization") String token
  ) {
    User user = getAuthenticatedUser(token);
    List<InvitoDTO> invitations = invitoService.getReceivedInvitations(
      user.getId()
    );
    return ResponseEntity.ok(invitations);
  }

  /**
   * Recupera gli inviti inviati dall'utente.
   *
   * @param token Il token di autorizzazione dell'utente.
   * @return Lista di InvitoDTO inviati.
   */
  @GetMapping("/sent")
  public ResponseEntity<List<InvitoDTO>> getSentInvitations(
    @RequestHeader("Authorization") String token
  ) {
    User user = getAuthenticatedUser(token);
    List<InvitoDTO> invitations = invitoService.getSentInvitations(
      user.getId()
    );
    return ResponseEntity.ok(invitations);
  }
}
