package hackhub.app.Presentation.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import hackhub.app.Application.Requests.CreaSegnalazioneRequest;
import hackhub.app.Application.Services.SegnalazioneService;
import hackhub.app.Core.POJO_Entities.Segnalazione;
import hackhub.app.Application.Utils.ISessionManager;
import hackhub.app.Core.POJO_Entities.User;
import hackhub.app.Presentation.Validators.SegnalazioneValidator;
import java.util.List;

@RestController
@RequestMapping("/api/segnalazioni")
public class SegnalazioneController {
    private final SegnalazioneService service;
    private final SegnalazioneValidator validator;
    private final ISessionManager sessionManager;

    @Autowired
    public SegnalazioneController(SegnalazioneService service, SegnalazioneValidator validator,
            ISessionManager sessionManager) {
        this.service = service;
        this.validator = validator;
        this.sessionManager = sessionManager;
    }

    @PostMapping("/crea")
    public ResponseEntity<?> creaSegnalazione(@RequestHeader("Authorization") String token,
            @RequestBody CreaSegnalazioneRequest request) {
        User user = sessionManager.getUser(token);
        if (user == null) {
            return ResponseEntity.status(401).body("Utente non autenticato.");
        }

        List<String> errors = validator.validateCreation(request);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body("Errore Validazione: " + String.join(", ", errors));
        }

        Segnalazione segnalazione = service.creaSegnalazione(request, user.getId());
        return ResponseEntity.ok(segnalazione);
    }

    @GetMapping
    public ResponseEntity<?> getSegnalazioni(@RequestParam String hackathonId,
            @RequestHeader("Authorization") String token) {
        User user = sessionManager.getUser(token);
        if (user == null) {
            return ResponseEntity.status(401).body("Utente non autenticato.");
        }

        if (hackathonId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Errore Validazione: ID non validi.");
        }
        List<Segnalazione> segnalazioni = service.getSegnalazioni(hackathonId, user.getId());
        return ResponseEntity.ok(segnalazioni);
    }
}
