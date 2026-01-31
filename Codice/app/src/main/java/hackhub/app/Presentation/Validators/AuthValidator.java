package hackhub.app.Presentation.Validators;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import hackhub.app.Application.Requests.LoginRequest;
import hackhub.app.Application.Requests.RegisterRequest;

@Component
public class AuthValidator extends AbstractValidator {

    public List<String> validateRegister(RegisterRequest request) {
        List<String> errors = new ArrayList<>();

        if (!validateRequestNotNull(request, errors)) {
            return errors;
        }

        validateRequired(request.getNome(), "Il nome è obbligatorio.", errors);
        validateRequired(request.getCognome(), "Il cognome è obbligatorio.", errors);
        validateEmail(request.getEmail(), errors, true);
        validateRequired(request.getPassword(), "La password è obbligatoria.", errors);

        return errors;
    }

    public List<String> validateLogin(LoginRequest request) {
        List<String> errors = new ArrayList<>();

        if (!validateRequestNotNull(request, errors)) {
            return errors;
        }

        validateEmail(request.getEmail(), errors, false);
        validateRequired(request.getPassword(), "La password è obbligatoria.", errors);

        return errors;
    }
}
