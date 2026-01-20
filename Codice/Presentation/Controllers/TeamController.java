package Presentation.Controllers;

import java.util.List;

import Application.Requests.CreaTeamRequest;
import Application.Services.TeamService;
import Core.POJO_Entities.Team;
import Presentation.Validators.CreaTeamValidator;

public class TeamController {
    private final TeamService teamService;
    private final CreaTeamValidator creaTeamValidator;

    public TeamController(TeamService teamService, CreaTeamValidator creaTeamValidator) {
        this.teamService = teamService;
        this.creaTeamValidator = creaTeamValidator;
    }

    // METODO ACCESSIBILE SOLO DA UN UTENTE SENZA TEAM
    public Object creaTeam(CreaTeamRequest request) {
        // 1. Validazione Input (Validation Pipeline)
        List<String> validationErrors = creaTeamValidator.validate(request);
        if (!validationErrors.isEmpty()) {
            // Ritorna un errore 400 Bad Request simulato
            return "Errore 400: Validazione fallita -> " + String.join(", ", validationErrors);
        }

        try {
            // 2. Chiamata al Business Service
            Team team = teamService.creaTeam(request);

            // 3. Risposta 200 OK (Mappata a DTO se necessario, qui ritorno l'oggetto per
            // semplicità)
            return "Successo 200: Team creato con ID " + team.getId();

        } catch (Exception e) {
            // Gestione errori di dominio (es. Utente non trovato, Ruoli errati)
            return "Errore 500: " + e.getMessage();
        }
    }
}
