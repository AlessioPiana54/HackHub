package hackhub.app.Presentation.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import hackhub.app.Application.Requests.CreaTeamRequest;
import hackhub.app.Application.Requests.IscriviTeamRequest;
import hackhub.app.Application.Services.TeamService;
import hackhub.app.Core.POJO_Entities.Partecipazione;
import hackhub.app.Core.POJO_Entities.Team;
import hackhub.app.Presentation.Validators.TeamValidator;
import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;
    private final TeamValidator teamValidator;

    @Autowired
    public TeamController(TeamService teamService, TeamValidator teamValidator) {
        this.teamService = teamService;
        this.teamValidator = teamValidator;
    }

    @PostMapping("/crea")
    public ResponseEntity<?> creaTeam(@RequestBody CreaTeamRequest request) {
        List<String> errors = teamValidator.validateCreation(request);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body("Errore Validazione: " + String.join(", ", errors));
        }

        Team team = teamService.creaTeam(request);
        return ResponseEntity.ok(team);
    }

    @PostMapping("/iscrivi")
    public ResponseEntity<?> iscriviTeam(@RequestBody IscriviTeamRequest request) {
        Partecipazione partecipazione = teamService.iscriviTeam(request);
        return ResponseEntity.ok(partecipazione);
    }
}
