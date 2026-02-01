package hackhub.app.Presentation.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import hackhub.app.Application.Requests.CreaInvitoRequest;
import hackhub.app.Application.Requests.RispostaInvitoRequest;
import hackhub.app.Application.Services.InvitoService;
import hackhub.app.Application.Utils.ISessionManager;
import hackhub.app.Core.POJO_Entities.User;
import hackhub.app.Core.POJO_Entities.Invito;
import hackhub.app.Presentation.Validators.InvitoValidator;

/**
 * Controller per la gestione degli inviti.
 */
@RestController
@RequestMapping("/api/inviti")
public class InvitoController extends AbstractController {

    private final InvitoService invitoService;
    private final InvitoValidator invitoValidator;

    public InvitoController(InvitoService invitoService, InvitoValidator invitoValidator,
            ISessionManager sessionManager) {
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
    @PostMapping("/invia")
    public ResponseEntity<?> inviaInvito(@RequestHeader("Authorization") String token,
            @RequestBody CreaInvitoRequest request) {
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
    @PostMapping("/risposta")
    public ResponseEntity<?> rispondiInvito(@RequestHeader("Authorization") String token,
            @RequestBody RispostaInvitoRequest request) {
        User user = getAuthenticatedUser(token);
        validateRequest(invitoValidator.validateRisposta(request));
        invitoService.gestisciRisposta(request, user.getId());
        return ResponseEntity.ok("Risposta registrata con successo.");
    }
}
