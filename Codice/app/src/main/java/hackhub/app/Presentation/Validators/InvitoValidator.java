package hackhub.app.Presentation.Validators;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import hackhub.app.Application.Requests.CreaInvitoRequest;
import hackhub.app.Application.Requests.RispostaInvitoRequest;

@Component
public class InvitoValidator extends AbstractValidator {

    public List<String> validateCreation(CreaInvitoRequest request) {
        List<String> errors = new ArrayList<>();

        if (!validateRequestNotNull(request, errors)) {
            return errors;
        }

        validateRequired(request.getTeamId(), "ID Team mancante.", errors);
        validateEmail(request.getEmailDestinatario(), errors, true);

        return errors;
    }

    public List<String> validateRisposta(RispostaInvitoRequest request) {
        List<String> errors = new ArrayList<>();

        if (!validateRequestNotNull(request, errors)) {
            return errors;
        }

        validateRequired(request.getInvitoId(), "ID Invito mancante.", errors);
        validateNotNull(request.isAccettato(), "Esito risposta mancante.", errors);

        return errors;
    }
}
