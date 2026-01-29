package hackhub.app.Presentation.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import hackhub.app.Application.Requests.CreaInvitoRequest;
import hackhub.app.Application.Requests.RispostaInvitoRequest;
import hackhub.app.Application.Services.InvitoService;
import hackhub.app.Application.Utils.ISessionManager;
import hackhub.app.Core.POJO_Entities.User;
import hackhub.app.Core.POJO_Entities.Invito;
import hackhub.app.Presentation.Validators.InvitoValidator;
import java.util.List;

@RestController
@RequestMapping("/api/inviti")
public class InvitoController {

    private final InvitoService invitoService;
    private final InvitoValidator invitoValidator;
    private final ISessionManager sessionManager;

    @Autowired
    public InvitoController(InvitoService invitoService, InvitoValidator invitoValidator,
            ISessionManager sessionManager) {
        this.invitoService = invitoService;
        this.invitoValidator = invitoValidator;
        this.sessionManager = sessionManager;
    }

    @PostMapping("/invia")
    public ResponseEntity<?> inviaInvito(@RequestHeader("Authorization") String token,
            @RequestBody CreaInvitoRequest request) {
        User user = sessionManager.getUser(token);
        if (user == null) {
            return ResponseEntity.status(401).body("Utente non autenticato.");
        }

        List<String> errors = invitoValidator.validateCreation(request);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body("Errore Validazione: " + String.join(", ", errors));
        }

        Invito invito = invitoService.inviaInvito(request, user.getId());
        return ResponseEntity.ok(invito);
    }

    @PostMapping("/risposta")
    public ResponseEntity<?> rispondiInvito(@RequestHeader("Authorization") String token,
            @RequestBody RispostaInvitoRequest request) {
        User user = sessionManager.getUser(token);
        if (user == null) {
            return ResponseEntity.status(401).body("Utente non autenticato.");
        }

        List<String> errors = invitoValidator.validateRisposta(request);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body("Errore Validazione: " + String.join(", ", errors));
        }

        invitoService.gestisciRisposta(request, user.getId());
        return ResponseEntity.ok("Risposta registrata con successo.");
    }
}
