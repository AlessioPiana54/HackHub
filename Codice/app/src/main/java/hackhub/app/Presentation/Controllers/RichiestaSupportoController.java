package hackhub.app.Presentation.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import hackhub.app.Application.Requests.CreaRichiestaSupportoRequest;
import hackhub.app.Application.Requests.ProponiCallRequest;
import hackhub.app.Application.Services.RichiestaSupportoService;
import hackhub.app.Core.POJO_Entities.RichiestaSupporto;
import hackhub.app.Application.Utils.ISessionManager;
import hackhub.app.Core.POJO_Entities.User;
import hackhub.app.Presentation.Validators.RichiestaSupportoValidator;
import java.util.List;

@RestController
@RequestMapping("/api/supporto")
public class RichiestaSupportoController {

    private final RichiestaSupportoService supportoService;
    private final RichiestaSupportoValidator validator;
    private final ISessionManager sessionManager;

    @Autowired
    public RichiestaSupportoController(RichiestaSupportoService supportoService, RichiestaSupportoValidator validator,
            ISessionManager sessionManager) {
        this.supportoService = supportoService;
        this.validator = validator;
        this.sessionManager = sessionManager;
    }

    @PostMapping("/crea")
    public ResponseEntity<?> creaRichiesta(@RequestHeader("Authorization") String token,
            @RequestBody CreaRichiestaSupportoRequest request) {
        User user = sessionManager.getUser(token);
        if (user == null) {
            return ResponseEntity.status(401).body("Utente non autenticato.");
        }

        List<String> errors = validator.validateCreation(request);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body("Errore Validazione: " + String.join(", ", errors));
        }

        RichiestaSupporto richiesta = supportoService.creaRichiesta(request, user.getId());
        return ResponseEntity.ok(richiesta);
    }

    @GetMapping("/mentore")
    public ResponseEntity<?> getRichiestePerMentore(@RequestParam String hackathonId,
            @RequestHeader("Authorization") String token) {
        User user = sessionManager.getUser(token);
        if (user == null) {
            return ResponseEntity.status(401).body("Utente non autenticato.");
        }

        if (hackathonId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Errore Validazione: ID non validi.");
        }
        List<RichiestaSupporto> richieste = supportoService.getRichiestePerMentore(hackathonId, user.getId());
        return ResponseEntity.ok(richieste);
    }

    @PostMapping("/proponi-call")
    public ResponseEntity<?> proponiCall(@RequestHeader("Authorization") String token,
            @RequestBody ProponiCallRequest request) {
        User user = sessionManager.getUser(token);
        if (user == null) {
            return ResponseEntity.status(401).body("Utente non autenticato.");
        }

        List<String> errors = validator.validateProponiCall(request);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body("Errore Validazione: " + String.join(", ", errors));
        }

        RichiestaSupporto richiesta = supportoService.proponiCall(request, user.getId());
        return ResponseEntity.ok(richiesta);
    }

    @GetMapping("/visualizza-proposte-call")
    public ResponseEntity<?> getRichiesteGestitePerTeam(@RequestParam String hackathonId, @RequestParam String teamId,
            @RequestHeader("Authorization") String token) {
        User user = sessionManager.getUser(token);
        if (user == null) {
            return ResponseEntity.status(401).body("Utente non autenticato.");
        }

        if (hackathonId.trim().isEmpty() || teamId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Errore Validazione: ID non validi.");
        }
        List<RichiestaSupporto> richieste = supportoService.getRichiesteGestitePerTeam(hackathonId, teamId,
                user.getId());
        return ResponseEntity.ok(richieste);
    }
}
