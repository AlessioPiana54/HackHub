package Presentation.Validators;

import java.util.ArrayList;
import java.util.List;

import Application.Requests.CreaTeamRequest;

public class TeamValidator {

    public List<String> validateCreation(CreaTeamRequest request) {
        List<String> errors = new ArrayList<>();

        // 1. Controllo difensivo se l'oggetto richiesta è nullo
        if (request == null) {
            errors.add("La richiesta non può essere nulla.");
            return errors; // Interrompiamo qui perché non possiamo accedere ai campi
        }

        // 2. Validazione Nome Team
        String nomeTeam = request.getNomeTeam();
        if (nomeTeam == null || nomeTeam.trim().isEmpty()) {
            errors.add("Il nome del team è obbligatorio.");
        } else if (nomeTeam.length() < 3 || nomeTeam.length() > 50) {
            errors.add("Il nome del team deve avere tra i 3 e i 50 caratteri.");
        }

        // 3. Validazione Leader Squadra
        String leader = request.getLeaderSquadra();
        if (leader == null || leader.trim().isEmpty()) {
            errors.add("L'ID del leader della squadra è obbligatorio.");
        }

        return errors;
    }
}
