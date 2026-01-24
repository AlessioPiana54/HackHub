package hackhub.app.Presentation.Validators;

import java.util.ArrayList;
import java.util.List;
import hackhub.app.Application.Requests.CreaSegnalazioneRequest;
import org.springframework.stereotype.Component;

@Component
public class SegnalazioneValidator {

    public List<String> validateCreation(CreaSegnalazioneRequest request) {
        List<String> errors = new ArrayList<>();

        if (request == null) {
            errors.add("La richiesta non può essere nulla.");
            return errors;
        }

        if (request.getIdHackathon() == null || request.getIdHackathon().trim().isEmpty()) {
            errors.add("ID Hackathon obbligatorio.");
        }
        if (request.getIdTeam() == null || request.getIdTeam().trim().isEmpty()) {
            errors.add("ID Team obbligatorio.");
        }
        if (request.getIdMentore() == null || request.getIdMentore().trim().isEmpty()) {
            errors.add("ID Mentore obbligatorio.");
        }
        if (request.getDescrizione() == null || request.getDescrizione().trim().isEmpty()) {
            errors.add("La descrizione è obbligatoria.");
        }

        return errors;
    }
}
