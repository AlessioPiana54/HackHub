package hackhub.app.Presentation.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import hackhub.app.Application.Requests.CreaHackathonRequest;
import hackhub.app.Application.Services.HackathonService;
import hackhub.app.Application.Utils.ISessionManager;
import hackhub.app.Core.POJO_Entities.Hackathon;
import hackhub.app.Core.POJO_Entities.User;
import hackhub.app.Presentation.Validators.HackathonValidator;
import java.util.List;

@RestController
@RequestMapping("/api/hackathons")
public class HackathonController {

    private final HackathonService hackathonService;
    private final HackathonValidator hackathonValidator;
    private final ISessionManager sessionManager;

    @Autowired
    public HackathonController(HackathonService hackathonService, HackathonValidator hackathonValidator,
            ISessionManager sessionManager) {
        this.hackathonService = hackathonService;
        this.hackathonValidator = hackathonValidator;
        this.sessionManager = sessionManager;
    }

    @PostMapping("/crea")
    public ResponseEntity<?> creaHackathon(@RequestHeader("Authorization") String token,
            @RequestBody CreaHackathonRequest request) {
        User user = sessionManager.getUser(token);
        if (user == null) {
            return ResponseEntity.status(401).body("Utente non autenticato.");
        }

        List<String> errors = hackathonValidator.validateCreation(request);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body("Errore Validazione: " + String.join(", ", errors));
        }

        Hackathon hackathon = hackathonService.creaHackathon(request, user.getId());
        return ResponseEntity.ok(hackathon);
    }

    @PostMapping("/{hackathonId}/terminaValutazione")
    public ResponseEntity<String> terminaFaseValutazione(@PathVariable String hackathonId,
            @RequestHeader("Authorization") String token) {
        User user = sessionManager.getUser(token);
        if (user == null) {
            return ResponseEntity.status(401).body("Utente non autenticato.");
        }

        if (hackathonId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Errore Validazione: ID non validi.");
        }
        hackathonService.terminaFaseValutazione(hackathonId, user.getId());
        return ResponseEntity.ok("Fase di valutazione terminata. Hackathon ora in fase di premiazione.");
    }

    @GetMapping("/{hackathonId}/classifica")
    public ResponseEntity<?> getClassifica(@PathVariable String hackathonId,
            @RequestHeader("Authorization") String token) {
        User user = sessionManager.getUser(token);
        if (user == null) {
            return ResponseEntity.status(401).body("Utente non autenticato.");
        }

        if (hackathonId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Errore Validazione: ID non validi.");
        }
        return ResponseEntity.ok(hackathonService.getClassifica(hackathonId, user.getId()));
    }

    @PostMapping("/{hackathonId}/vincitore")
    public ResponseEntity<String> proclamaVincitore(@PathVariable String hackathonId, @RequestParam String teamId,
            @RequestHeader("Authorization") String token) {
        User user = sessionManager.getUser(token);
        if (user == null) {
            return ResponseEntity.status(401).body("Utente non autenticato.");
        }

        if (hackathonId.trim().isEmpty() || teamId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Errore Validazione: ID non validi.");
        }
        hackathonService.proclamaVincitore(hackathonId, teamId, user.getId());
        return ResponseEntity.ok("Vincitore proclamato e Hackathon concluso.");
    }
}
