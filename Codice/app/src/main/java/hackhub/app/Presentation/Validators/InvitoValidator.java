package hackhub.app.Presentation.Validators;

import hackhub.app.Application.Requests.CreaInvitoRequest;
import hackhub.app.Application.Requests.RispostaInvitoRequest;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Validatore per le richieste relative agli inviti.
 */
@Component
public class InvitoValidator extends AbstractValidator {

  /**
   * Valida la creazione di un nuovo invito.
   *
   * @param request i dati della richiesta
   * @return una lista di errori, vuota se i dati sono validi
   */
  public List<String> validateCreation(CreaInvitoRequest request) {
    List<String> errors = new ArrayList<>();

    if (!validateRequestNotNull(request, errors)) {
      return errors;
    }

    validateRequired(request.getTeamId(), "ID Team mancante.", errors);
    validateRequired(request.getEmailDestinatario(), "Email destinatario mancante.", errors);
    validateEmail(request.getEmailDestinatario(), errors, true);

    return errors;
  }

  /**
   * Valida la risposta a un invito.
   *
   * @param request i dati della risposta
   * @return una lista di errori, vuota se i dati sono validi
   */
  public List<String> validateRisposta(RispostaInvitoRequest request) {
    List<String> errors = new ArrayList<>();

    if (!validateRequestNotNull(request, errors)) {
      return errors;
    }

    validateNotNull(
      request.getAccettato(),
      "La risposta all'invito è obbligatoria.",
      errors
    );

    return errors;
  }
}
