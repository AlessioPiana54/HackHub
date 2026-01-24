package hackhub.app.Presentation.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import hackhub.app.Application.Requests.CreaSegnalazioneRequest;
import hackhub.app.Application.Services.SegnalazioneService;
import hackhub.app.Core.POJO_Entities.Segnalazione;
import hackhub.app.Presentation.Validators.SegnalazioneValidator;
import java.util.List;

@RestController
@RequestMapping("/api/segnalazioni")
public class SegnalazioneController {
    private final SegnalazioneService service;
    private final SegnalazioneValidator validator;

    @Autowired
    public SegnalazioneController(SegnalazioneService service, SegnalazioneValidator validator) {
        this.service = service;
        this.validator = validator;
    }

    @PostMapping("/crea")
    public ResponseEntity<?> creaSegnalazione(@RequestBody CreaSegnalazioneRequest request) {
        List<String> errors = validator.validateCreation(request);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body("Errore Validazione: " + String.join(", ", errors));
        }

        Segnalazione segnalazione = service.creaSegnalazione(request);
        return ResponseEntity.ok(segnalazione);
    }

    @GetMapping
    public ResponseEntity<?> getSegnalazioni(@RequestParam String hackathonId, @RequestParam String organizerId) {
        List<Segnalazione> segnalazioni = service.getSegnalazioni(hackathonId, organizerId);
        return ResponseEntity.ok(segnalazioni);
    }
}
