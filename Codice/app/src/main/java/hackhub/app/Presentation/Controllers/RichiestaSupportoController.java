package hackhub.app.Presentation.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import hackhub.app.Application.Requests.CreaRichiestaSupportoRequest;
import hackhub.app.Application.Requests.ProponiCallRequest;
import hackhub.app.Application.Services.RichiestaSupportoService;
import hackhub.app.Core.POJO_Entities.RichiestaSupporto;
import hackhub.app.Presentation.Validators.RichiestaSupportoValidator;
import java.util.List;

@RestController
@RequestMapping("/api/supporto")
public class RichiestaSupportoController {

    private final RichiestaSupportoService supportoService;
    private final RichiestaSupportoValidator validator;

    @Autowired
    public RichiestaSupportoController(RichiestaSupportoService supportoService, RichiestaSupportoValidator validator) {
        this.supportoService = supportoService;
        this.validator = validator;
    }

    @PostMapping("/crea")
    public ResponseEntity<?> creaRichiesta(@RequestBody CreaRichiestaSupportoRequest request) {
        List<String> errors = validator.validateCreation(request);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body("Errore Validazione: " + String.join(", ", errors));
        }

        RichiestaSupporto richiesta = supportoService.creaRichiesta(request);
        return ResponseEntity.ok(richiesta);
    }

    @GetMapping
    public ResponseEntity<?> getRichiestePerMentore(@RequestParam String hackathonId, @RequestParam String mentorId) {
        if (hackathonId.trim().isEmpty() || mentorId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Errore Validazione: ID non validi.");
        }
        List<RichiestaSupporto> richieste = supportoService.getRichiestePerMentore(hackathonId, mentorId);
        return ResponseEntity.ok(richieste);
    }

    @PostMapping("/proponi-call")
    public ResponseEntity<?> proponiCall(@RequestBody ProponiCallRequest request) {
        List<String> errors = validator.validateProponiCall(request);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body("Errore Validazione: " + String.join(", ", errors));
        }

        RichiestaSupporto richiesta = supportoService.proponiCall(request);
        return ResponseEntity.ok(richiesta);
    }

    @GetMapping("/visualizza-richieste-con-risposta-team")
    public ResponseEntity<?> getRichiesteGestitePerTeam(@RequestParam String hackathonId, @RequestParam String teamId,
            @RequestParam String userId) {
        if (hackathonId.trim().isEmpty() || teamId.trim().isEmpty() || userId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Errore Validazione: ID non validi.");
        }
        List<RichiestaSupporto> richieste = supportoService.getRichiesteGestitePerTeam(hackathonId, teamId, userId);
        return ResponseEntity.ok(richieste);
    }
}
