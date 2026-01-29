package hackhub.app.Presentation.Validators;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import hackhub.app.Application.Requests.LoginRequest;
import hackhub.app.Application.Requests.RegisterRequest;

@Component
public class AuthValidator {

    // Serve per validare l'email assicurandosi che sia testo@dominio.estensione e non contenga caratteri speciali
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    public List<String> validateRegister(RegisterRequest request) {
        List<String> errors = new ArrayList<>();

        if (request == null) {
            errors.add("La richiesta non può essere nulla.");
            return errors;
        }

        if (request.getNome() == null || request.getNome().trim().isEmpty()) {
            errors.add("Il nome è obbligatorio.");
        }

        if (request.getCognome() == null || request.getCognome().trim().isEmpty()) {
            errors.add("Il cognome è obbligatorio.");
        }

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            errors.add("L'email è obbligatoria.");
        } else if (!request.getEmail().matches(EMAIL_PATTERN)) {
            errors.add("Formato email non valido.");
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            errors.add("La password è obbligatoria.");
        }

        return errors;
    }

    public List<String> validateLogin(LoginRequest request) {
        List<String> errors = new ArrayList<>();

        if (request == null) {
            errors.add("La richiesta non può essere nulla.");
            return errors;
        }

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            errors.add("L'email è obbligatoria.");
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            errors.add("La password è obbligatoria.");
        }

        return errors;
    }
}
