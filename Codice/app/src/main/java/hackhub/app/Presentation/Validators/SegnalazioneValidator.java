package hackhub.app.Presentation.Validators;

import java.util.ArrayList;
import java.util.List;
import hackhub.app.Application.Requests.CreaSegnalazioneRequest;
import org.springframework.stereotype.Component;

@Component
public class SegnalazioneValidator extends AbstractValidator {

    public List<String> validateCreation(CreaSegnalazioneRequest request) {
        List<String> errors = new ArrayList<>();

        if (!validateRequestNotNull(request, errors)) {
            return errors;
        }

        validateRequired(request.getIdHackathon(), "ID Hackathon obbligatorio.", errors);
        validateRequired(request.getIdTeam(), "ID Team obbligatorio.", errors);
        validateRequired(request.getDescrizione(), "La descrizione è obbligatoria.", errors);

        return errors;
    }
}
