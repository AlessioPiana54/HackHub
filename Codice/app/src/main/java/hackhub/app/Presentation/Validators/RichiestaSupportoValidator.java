package hackhub.app.Presentation.Validators;

import hackhub.app.Application.Requests.CreaRichiestaSupportoRequest;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class RichiestaSupportoValidator {

    public List<String> validateCreation(CreaRichiestaSupportoRequest request) {
        List<String> errors = new ArrayList<>();

        if (request == null) {
            errors.add("La richiesta non può essere nulla.");
            return errors;
        }

        if (request.getHackathonId() == null || request.getHackathonId().trim().isEmpty()) {
            errors.add("ID Hackathon obbligatorio.");
        }
        if (request.getTeamId() == null || request.getTeamId().trim().isEmpty()) {
            errors.add("ID Team obbligatorio.");
        }
        if (request.getRichiedenteId() == null || request.getRichiedenteId().trim().isEmpty()) {
            errors.add("ID Richiedente obbligatorio.");
        }
        if (request.getDescrizione() == null || request.getDescrizione().trim().isEmpty()) {
            errors.add("La descrizione è obbligatoria.");
        }

        return errors;
    }
}
