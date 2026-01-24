package hackhub.app.Presentation.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import hackhub.app.Application.Requests.CreaValutazioneRequest;
import hackhub.app.Application.Requests.InviaSottomissioneRequest;
import hackhub.app.Application.Services.SottomissioneService;
import hackhub.app.Core.POJO_Entities.Sottomissione;
import hackhub.app.Core.POJO_Entities.Valutazione;
import hackhub.app.Presentation.Validators.SottomissioneValidator;
import hackhub.app.Presentation.Validators.ValutazioneValidator;
import java.util.List;

@RestController
@RequestMapping("/api/sottomissioni")
public class SottomissioneController {

    private final SottomissioneService sottomissioneService;
    private final SottomissioneValidator sottomissioneValidator;
    private final ValutazioneValidator valutazioneValidator;

    @Autowired
    public SottomissioneController(SottomissioneService sottomissioneService,
            SottomissioneValidator sottomissioneValidator,
            ValutazioneValidator valutazioneValidator) {
        this.sottomissioneService = sottomissioneService;
        this.sottomissioneValidator = sottomissioneValidator;
        this.valutazioneValidator = valutazioneValidator;
    }

    @PostMapping("/invia")
    public ResponseEntity<?> inviaSottomissione(@RequestBody InviaSottomissioneRequest request) {
        List<String> errors = sottomissioneValidator.validateCreation(request);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body("Errore Validazione: " + String.join(", ", errors));
        }

        Sottomissione sottomissione = sottomissioneService.inviaSottomissione(request);
        return ResponseEntity.ok(sottomissione);
    }

    @PostMapping("/valuta")
    public ResponseEntity<?> valutaSottomissione(@RequestBody CreaValutazioneRequest request) {
        List<String> errors = valutazioneValidator.validateCreation(request);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body("Errore Validazione: " + String.join(", ", errors));
        }

        Valutazione valutazione = sottomissioneService.valutaSottomissione(request);
        return ResponseEntity.ok(valutazione);
    }
}
