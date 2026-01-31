package hackhub.app.Presentation.Validators;

import hackhub.app.Application.Requests.CreaValutazioneRequest;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ValutazioneValidator extends AbstractValidator {
    public List<String> validateCreation(CreaValutazioneRequest request) {
        List<String> errors = new ArrayList<>();

        if (!validateRequestNotNull(request, errors)) {
            return errors;
        }

        validateRequired(request.getIdSottomissione(), "ID Sottomissione mancante.", errors);
        validateRequired(request.getGiudizio(), "Il giudizio è obbligatorio.", errors);

        if (request.getVoto() < 0 || request.getVoto() > 10) {
            errors.add("Il voto deve essere compreso tra 0 e 10.");
        }

        return errors;
    }
}
