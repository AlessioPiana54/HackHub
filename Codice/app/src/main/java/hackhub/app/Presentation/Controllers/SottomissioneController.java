package hackhub.app.Presentation.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import hackhub.app.Application.Requests.CreaValutazioneRequest;
import hackhub.app.Application.Requests.InviaSottomissioneRequest;
import hackhub.app.Application.Requests.ModificaSottomissioneRequest;
import hackhub.app.Application.Services.SottomissioneService;
import hackhub.app.Core.POJO_Entities.Sottomissione;
import hackhub.app.Application.Utils.ISessionManager;
import hackhub.app.Core.POJO_Entities.User;
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
    private final ISessionManager sessionManager;

    @Autowired
    public SottomissioneController(SottomissioneService sottomissioneService,
            SottomissioneValidator sottomissioneValidator,
            ValutazioneValidator valutazioneValidator,
            ISessionManager sessionManager) {
        this.sottomissioneService = sottomissioneService;
        this.sottomissioneValidator = sottomissioneValidator;
        this.valutazioneValidator = valutazioneValidator;
        this.sessionManager = sessionManager;
    }

    @PostMapping("/invia")
    public ResponseEntity<?> inviaSottomissione(@RequestHeader("Authorization") String token,
            @RequestBody InviaSottomissioneRequest request) {
        User user = sessionManager.getUser(token);
        if (user == null) {
            return ResponseEntity.status(401).body("Utente non autenticato.");
        }

        List<String> errors = sottomissioneValidator.validateCreation(request);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body("Errore Validazione: " + String.join(", ", errors));
        }

        Sottomissione sottomissione = sottomissioneService.inviaSottomissione(request, user.getId());
        return ResponseEntity.ok(sottomissione);
    }

    @PutMapping("/modifica")
    public ResponseEntity<?> modificaSottomissione(@RequestHeader("Authorization") String token,
            @RequestBody ModificaSottomissioneRequest request) {
        User user = sessionManager.getUser(token);
        if (user == null) {
            return ResponseEntity.status(401).body("Utente non autenticato.");
        }

        List<String> errors = sottomissioneValidator.validateModification(request);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body("Errore Validazione: " + String.join(", ", errors));
        }

        Sottomissione sottomissione = sottomissioneService.modificaSottomissione(request, user.getId());
        return ResponseEntity.ok(sottomissione);
    }

    @PostMapping("/valuta")
    public ResponseEntity<?> valutaSottomissione(@RequestHeader("Authorization") String token,
            @RequestBody CreaValutazioneRequest request) {
        User user = sessionManager.getUser(token);
        if (user == null) {
            return ResponseEntity.status(401).body("Utente non autenticato.");
        }

        List<String> errors = valutazioneValidator.validateCreation(request);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body("Errore Validazione: " + String.join(", ", errors));
        }

        Valutazione valutazione = sottomissioneService.valutaSottomissione(request, user.getId());
        return ResponseEntity.ok(valutazione);
    }
}
