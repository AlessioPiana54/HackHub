package hackhub.app.Presentation.Validators;

import hackhub.app.Application.Requests.CreaRichiestaSupportoRequest;
import hackhub.app.Application.Requests.ProponiCallRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class RichiestaSupportoValidator extends AbstractValidator {

    public List<String> validateCreation(CreaRichiestaSupportoRequest request) {
        List<String> errors = new ArrayList<>();

        if (!validateRequestNotNull(request, errors)) {
            return errors;
        }

        validateRequired(request.getHackathonId(), "ID Hackathon obbligatorio.", errors);
        validateRequired(request.getTeamId(), "ID Team obbligatorio.", errors);
        validateRequired(request.getDescrizione(), "La descrizione è obbligatoria.", errors);

        return errors;

    }

    public List<String> validateProponiCall(ProponiCallRequest request) {
        List<String> errors = new ArrayList<>();

        if (!validateRequestNotNull(request, errors)) {
            return errors;
        }

        validateRequired(request.getRichiestaId(), "ID Richiesta Supporto obbligatorio.", errors);
        validateRequired(request.getLinkCall(), "Il link per la call è obbligatorio.", errors);

        if (request.getDataCall() == null) {
            errors.add("La data della call è obbligatoria.");
        } else if (request.getDataCall().isBefore(LocalDateTime.now())) {
            errors.add("La data della call deve essere successiva alla data odierna.");
        }

        return errors;
    }
}
