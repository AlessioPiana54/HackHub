package hackhub.app.Presentation.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import hackhub.app.Application.Requests.CreaInvitoRequest;
import hackhub.app.Application.Requests.RispostaInvitoRequest;
import hackhub.app.Application.Services.InvitoService;
import hackhub.app.Core.POJO_Entities.Invito;
import hackhub.app.Presentation.Validators.InvitoValidator;
import java.util.List;

@RestController
@RequestMapping("/api/inviti")
public class InvitoController {

    private final InvitoService invitoService;
    private final InvitoValidator invitoValidator;

    @Autowired
    public InvitoController(InvitoService invitoService, InvitoValidator invitoValidator) {
        this.invitoService = invitoService;
        this.invitoValidator = invitoValidator;
    }

    @PostMapping("/invia")
    public ResponseEntity<?> inviaInvito(@RequestBody CreaInvitoRequest request) {
        List<String> errors = invitoValidator.validateCreation(request);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body("Errore Validazione: " + String.join(", ", errors));
        }

        Invito invito = invitoService.inviaInvito(request);
        return ResponseEntity.ok(invito);
    }

    @PostMapping("/risposta")
    public ResponseEntity<?> rispondiInvito(@RequestBody RispostaInvitoRequest request) {
        List<String> errors = invitoValidator.validateRisposta(request);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body("Errore Validazione: " + String.join(", ", errors));
        }

        invitoService.gestisciRisposta(request);
        return ResponseEntity.ok("Risposta registrata con successo.");
    }
}
