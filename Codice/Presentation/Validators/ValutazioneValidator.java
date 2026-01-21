package Presentation.Validators;

import Application.Requests.CreaValutazioneRequest;
import java.util.ArrayList;
import java.util.List;

public class ValutazioneValidator {
    public List<String> validateCreation(CreaValutazioneRequest request) {
        List<String> errors = new ArrayList<>();

        if (request.getIdSottomissione() == null || request.getIdSottomissione().isEmpty()) {
            errors.add("ID Sottomissione mancante.");
        }

        if (request.getIdGiudice() == null || request.getIdGiudice().isEmpty()) {
            errors.add("ID Giudice mancante.");
        }

        if (request.getVoto() < 0 || request.getVoto() > 10) {
            errors.add("Il voto deve essere compreso tra 0 e 10.");
        }

        if (request.getGiudizio() == null || request.getGiudizio().trim().isEmpty()) {
            errors.add("Il giudizio è obbligatorio.");
        }

        return errors;
    }
}
