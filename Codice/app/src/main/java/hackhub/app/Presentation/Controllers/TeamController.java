package hackhub.app.Presentation.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import hackhub.app.Application.Requests.CreaTeamRequest;
import hackhub.app.Application.Requests.IscriviTeamRequest;
import hackhub.app.Application.Services.TeamService;
import hackhub.app.Application.Utils.ISessionManager;
import hackhub.app.Core.POJO_Entities.User;
import hackhub.app.Core.POJO_Entities.Partecipazione;
import hackhub.app.Core.POJO_Entities.Team;
import hackhub.app.Presentation.Validators.TeamValidator;
import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;
    private final TeamValidator teamValidator;
    private final ISessionManager sessionManager;

    @Autowired
    public TeamController(TeamService teamService, TeamValidator teamValidator,
            ISessionManager sessionManager) {
        this.teamService = teamService;
        this.teamValidator = teamValidator;
        this.sessionManager = sessionManager;
    }

    @PostMapping("/crea")
    public ResponseEntity<?> creaTeam(@RequestHeader("Authorization") String token,
            @RequestBody CreaTeamRequest request) {
        User user = sessionManager.getUser(token);
        if (user == null) {
            return ResponseEntity.status(401).body("Utente non autenticato.");
        }

        List<String> errors = teamValidator.validateCreation(request);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body("Errore Validazione: " + String.join(", ", errors));
        }

        Team team = teamService.creaTeam(request, user.getId());
        return ResponseEntity.ok(team);
    }

    @PostMapping("/iscrivi")
    public ResponseEntity<?> iscriviTeam(@RequestHeader("Authorization") String token,
            @RequestBody IscriviTeamRequest request) {
        User user = sessionManager.getUser(token);
        if (user == null) {
            return ResponseEntity.status(401).body("Utente non autenticato.");
        }

        Partecipazione partecipazione = teamService.iscriviTeam(request, user.getId());
        return ResponseEntity.ok(partecipazione);
    }
}
