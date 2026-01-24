package hackhub.app.Presentation.Validators;

import hackhub.app.Application.Requests.InviaSottomissioneRequest;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SottomissioneValidator {

    public List<String> validateCreation(InviaSottomissioneRequest request) {
        List<String> errors = new ArrayList<>();

        if (request == null) {
            errors.add("La richiesta non può essere nulla.");
            return errors;
        }

        if (request.getIdHackathon() == null || request.getIdHackathon().trim().isEmpty()) {
            errors.add("L'ID dell'Hackathon è obbligatorio.");
        }

        if (request.getIdTeam() == null || request.getIdTeam().trim().isEmpty()) {
            errors.add("L'ID del Team è obbligatorio.");
        }

        if (request.getIdUtente() == null || request.getIdUtente().trim().isEmpty()) {
            errors.add("L'ID dell'Utente è obbligatorio.");
        }

        if (request.getLinkProgetto() == null || request.getLinkProgetto().trim().isEmpty()) {
            errors.add("Il link al progetto è obbligatorio.");
        }

        return errors;
    }
}
