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

/**
 * Controller per la gestione dei team.
 */
@RestController
@RequestMapping("/api/teams")
public class TeamController extends AbstractController {

    private final TeamService teamService;
    private final TeamValidator teamValidator;

    @Autowired
    public TeamController(TeamService teamService, TeamValidator teamValidator,
            ISessionManager sessionManager) {
        super(sessionManager);
        this.teamService = teamService;
        this.teamValidator = teamValidator;
    }

    /**
     * Crea un nuovo team.
     *
     * @param token   Il token di autorizzazione del creatore del team.
     * @param request I dati per la creazione del team.
     * @return Il team creato o un errore di validazione.
     */
    @PostMapping("/crea")
    public ResponseEntity<?> creaTeam(@RequestHeader("Authorization") String token,
            @RequestBody CreaTeamRequest request) {
        User user = getAuthenticatedUser(token);
        validateRequest(teamValidator.validateCreation(request));
        Team team = teamService.creaTeam(request, user.getId());
        return ResponseEntity.ok(team);
    }

    /**
     * Iscrive un team a un hackathon.
     *
     * @param token       Il token di autorizzazione del leader del team.
     * @param teamId      L'ID del team da iscrivere.
     * @param hackathonId L'ID dell'hackathon a cui iscriversi.
     * @return La partecipazione creata o un errore di validazione.
     */
    @PostMapping("/{teamId}/iscrivi")
    public ResponseEntity<?> iscriviTeam(@RequestHeader("Authorization") String token,
            @PathVariable String teamId, @RequestParam String hackathonId) {
        User user = getAuthenticatedUser(token);
        validateIds(teamId, hackathonId);
        Partecipazione partecipazione = teamService.iscriviTeam(teamId, hackathonId, user.getId());
        return ResponseEntity.ok(partecipazione);
    }

    /**
     * Permette a un utente di abbandonare un team.
     *
     * @param token  Il token di autorizzazione dell'utente.
     * @param teamId L'ID del team da abbandonare.
     * @return Un messaggio di conferma.
     */
    @PostMapping("/{teamId}/abbandona")
    public ResponseEntity<?> abbandonaTeam(@RequestHeader("Authorization") String token,
            @PathVariable String teamId) {
        User user = getAuthenticatedUser(token);
        validateIds(teamId);
        teamService.abbandonaTeam(teamId, user.getId());
        return ResponseEntity.ok("Hai abbandonato il team con successo.");
    }
}
