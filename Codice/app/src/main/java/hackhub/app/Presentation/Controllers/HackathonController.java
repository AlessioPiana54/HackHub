package hackhub.app.Presentation.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import hackhub.app.Application.Requests.CreaHackathonRequest;
import hackhub.app.Application.Services.HackathonService;
import hackhub.app.Core.POJO_Entities.Hackathon;
import hackhub.app.Presentation.Validators.HackathonValidator;
import java.util.List;

@RestController
@RequestMapping("/api/hackathons")
public class HackathonController {

    private final HackathonService hackathonService;
    private final HackathonValidator hackathonValidator;

    @Autowired
    public HackathonController(HackathonService hackathonService, HackathonValidator hackathonValidator) {
        this.hackathonService = hackathonService;
        this.hackathonValidator = hackathonValidator;
    }

    @PostMapping("/crea")
    public ResponseEntity<?> creaHackathon(@RequestBody CreaHackathonRequest request) {
        List<String> errors = hackathonValidator.validateCreation(request);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body("Errore Validazione: " + String.join(", ", errors));
        }

        Hackathon hackathon = hackathonService.creaHackathon(request);
        return ResponseEntity.ok(hackathon);
    }
}
