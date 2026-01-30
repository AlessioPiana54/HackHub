package hackhub.app.Presentation.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import hackhub.app.Application.Requests.CreaTeamRequest;
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

    @PostMapping("/{teamId}/iscrivi")
    public ResponseEntity<?> iscriviTeam(@RequestHeader("Authorization") String token,
            @PathVariable String teamId, @RequestParam String hackathonId) {
        User user = sessionManager.getUser(token);
        if (user == null) {
            return ResponseEntity.status(401).body("Utente non autenticato.");
        }

        if (hackathonId.trim().isEmpty() || teamId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Errore Validazione: ID non validi.");
        }

        Partecipazione partecipazione = teamService.iscriviTeam(teamId, hackathonId, user.getId());
        return ResponseEntity.ok(partecipazione);
    }

    @PostMapping("/{teamId}/abbandona")
    public ResponseEntity<?> abbandonaTeam(@RequestHeader("Authorization") String token,
            @PathVariable String teamId) {
        User user = sessionManager.getUser(token);
        if (user == null) {
            return ResponseEntity.status(401).body("Utente non autenticato.");
        }

        if (teamId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Errore Validazione: ID non validi.");
        }

        teamService.abbandonaTeam(teamId, user.getId());
        return ResponseEntity.ok("Hai abbandonato il team con successo.");
    }
}
