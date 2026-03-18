package hackhub.app.Presentation.Validators;

import hackhub.app.Application.Requests.InviaSottomissioneRequest;
import hackhub.app.Application.Requests.ModificaSottomissioneRequest;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SottomissioneValidator extends AbstractValidator {

    public List<String> validateCreation(InviaSottomissioneRequest request) {
        List<String> errors = new ArrayList<>();

        if (!validateRequestNotNull(request, errors)) {
            return errors;
        }

        validateRequired(request.getIdHackathon(), "L'ID dell'Hackathon è obbligatorio.", errors);
        validateRequired(request.getIdTeam(), "L'ID del Team è obbligatorio.", errors);
        validateRequired(request.getLinkProgetto(), "Il link al progetto è obbligatorio.", errors);

        return errors;
    }

    public List<String> validateModification(ModificaSottomissioneRequest request) {
        List<String> errors = new ArrayList<>();

        if (!validateRequestNotNull(request, errors)) {
            return errors;
        }

        validateRequired(request.getLinkProgetto(), "Il link al progetto è obbligatorio.", errors);

        return errors;
    }
}
