package hackhub.app.Presentation.Validators;

import hackhub.app.Application.Requests.CreaRichiestaSupportoRequest;
import hackhub.app.Application.Requests.ProponiCallRequest;
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
        if (request.getDescrizione() == null || request.getDescrizione().trim().isEmpty()) {
            errors.add("La descrizione è obbligatoria.");
        }

        return errors;

    }

    public List<String> validateProponiCall(ProponiCallRequest request) {
        List<String> errors = new ArrayList<>();

        if (request == null) {
            errors.add("La richiesta non può essere nulla.");
            return errors;
        }

        if (request.getRichiestaId() == null || request.getRichiestaId().trim().isEmpty()) {
            errors.add("ID Richiesta Supporto obbligatorio.");
        }
        if (request.getLinkCall() == null || request.getLinkCall().trim().isEmpty()) {
            errors.add("Il link per la call è obbligatorio.");
        }
        if (request.getDataCall() == null) {
            errors.add("La data della call è obbligatoria.");
        } else if (request.getDataCall().isBefore(java.time.LocalDateTime.now())) {
            errors.add("La data della call deve essere successiva alla data odierna.");
        }

        return errors;
    }
}
