package hackhub.app.Presentation.Validators;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import hackhub.app.Application.Requests.CreaInvitoRequest;
import hackhub.app.Application.Requests.RispostaInvitoRequest;

@Component
public class InvitoValidator {

    public List<String> validateCreation(CreaInvitoRequest request) {
        List<String> errors = new ArrayList<>();

        if (request == null) {
            errors.add("La richiesta non può essere nulla.");
            return errors;
        }

        if (request.getTeamId() == null || request.getTeamId().trim().isEmpty()) {
            errors.add("ID Team mancante.");
        }
        if (request.getEmailDestinatario() == null || request.getEmailDestinatario().trim().isEmpty()) {
            errors.add("Email destinatario mancante.");
        }
        // Validazione email semplice opzionale
        if (request.getEmailDestinatario() != null && !request.getEmailDestinatario().contains("@")) {
            errors.add("Email destinatario non valida.");
        }

        return errors;
    }

    public List<String> validateRisposta(RispostaInvitoRequest request) {
        List<String> errors = new ArrayList<>();

        if (request == null) {
            errors.add("La richiesta non può essere nulla.");
            return errors;
        }

        if (request.getInvitoId() == null || request.getInvitoId().trim().isEmpty()) {
            errors.add("ID Invito mancante.");
        }
        if (request.isAccettato() == null) {
            errors.add("Esito risposta mancante.");
        }

        return errors;
    }
}
