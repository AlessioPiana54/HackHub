package Presentation.Controllers;

import java.util.List;

import Application.Requests.CreaTeamRequest;
import Application.Requests.IscriviTeamRequest;
import Application.Services.TeamService;
import Core.POJO_Entities.Partecipazione;
import Core.POJO_Entities.Team;
import Presentation.Validators.TeamValidator;

public class TeamController {
    private final TeamService teamService;
    private final TeamValidator creaTeamValidator;

    public TeamController(TeamService teamService, TeamValidator creaTeamValidator) {
        this.teamService = teamService;
        this.creaTeamValidator = creaTeamValidator;
    }

    // METODO ACCESSIBILE SOLO DA UN UTENTE SENZA TEAM
    public Object creaTeam(CreaTeamRequest request) {
        // 1. Validazione Input (Validation Pipeline)
        List<String> validationErrors = creaTeamValidator.validateCreation(request);
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

    // METODO ACCESSIBILE SOLO DAL LEADER DEL TEAM
    public Object iscriviTeam(IscriviTeamRequest request) {
        // Validazione Input (potrebbe servire un nuovo validator o semplice null check)
        if (request.getIdTeam() == null || request.getIdHackathon() == null || request.getIdRichiedente() == null
                || request.getIdTeam().isEmpty() || request.getIdHackathon().isEmpty()
                || request.getIdRichiedente().isEmpty()) {
            return "Errore 400: Validazione fallita -> ID mancanti";
        }

        try {
            Partecipazione p = teamService.iscriviTeam(request);
            return "Successo 200: Team iscritto con ID Partecipazione " + p.getId();
        } catch (Exception e) {
            return "Errore 500: " + e.getMessage();
        }
    }
}
