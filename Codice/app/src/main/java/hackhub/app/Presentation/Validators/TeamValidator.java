package hackhub.app.Presentation.Validators;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import hackhub.app.Application.Requests.CreaTeamRequest;

@Component
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

        return errors;
    }
}
