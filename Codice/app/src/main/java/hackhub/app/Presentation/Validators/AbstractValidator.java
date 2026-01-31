package hackhub.app.Presentation.Validators;

import java.util.List;

/**
 * Classe base astratta per i validatori.
 * Fornisce metodi comuni per la validazione di campi obbligatori, email e
 * oggetti non nulli.
 */
public abstract class AbstractValidator {

    protected static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    /**
     * Valida che l'oggetto richiesta non sia nullo.
     *
     * @param request L'oggetto richiesta da validare.
     * @param errors  La lista di errori a cui aggiungere un eventuale messaggio.
     * @return true se la richiesta non è nulla, false altrimenti.
     */
    protected boolean validateRequestNotNull(Object request, List<String> errors) {
        if (request == null) {
            errors.add("La richiesta non può essere nulla.");
            return false;
        }
        return true;
    }

    /**
     * Valida che una stringa obbligatoria non sia nulla o vuota.
     *
     * @param value        La stringa da validare.
     * @param errorMessage Il messaggio di errore da aggiungere se la validazione
     *                     fallisce.
     * @param errors       La lista di errori.
     */
    protected void validateRequired(String value, String errorMessage, List<String> errors) {
        if (value == null || value.trim().isEmpty()) {
            errors.add(errorMessage);
        }
    }

    /**
     * Valida che un oggetto obbligatorio non sia nullo.
     *
     * @param value        L'oggetto da validare.
     * @param errorMessage Il messaggio di errore da aggiungere se la validazione
     *                     fallisce.
     * @param errors       La lista di errori.
     */
    protected void validateNotNull(Object value, String errorMessage, List<String> errors) {
        if (value == null) {
            errors.add(errorMessage);
        }
    }

    /**
     * Valida un indirizzo email.
     *
     * @param email        L'indirizzo email da validare.
     * @param errors       La lista di errori.
     * @param checkPattern Se true, verifica anche il formato dell'email tramite
     *                     regex.
     */
    protected void validateEmail(String email, List<String> errors, boolean checkPattern) {
        if (email == null || email.trim().isEmpty()) {
            errors.add("L'email è obbligatoria.");
        } else if (checkPattern && !email.matches(EMAIL_PATTERN)) {
            errors.add("Formato email non valido.");
        }
    }
}
