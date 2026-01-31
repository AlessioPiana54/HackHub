package hackhub.app.Presentation.Validators;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import hackhub.app.Application.Requests.CreaTeamRequest;

@Component
public class TeamValidator extends AbstractValidator {

    public List<String> validateCreation(CreaTeamRequest request) {
        List<String> errors = new ArrayList<>();

        if (!validateRequestNotNull(request, errors)) {
            return errors;
        }

        // Validazione Nome Team
        String nomeTeam = request.getNomeTeam();
        validateRequired(nomeTeam, "Il nome del team è obbligatorio.", errors);

        if (nomeTeam != null && (nomeTeam.length() < 3 || nomeTeam.length() > 50)) {
            errors.add("Il nome del team deve avere tra i 3 e i 50 caratteri.");
        }

        return errors;
    }
}
