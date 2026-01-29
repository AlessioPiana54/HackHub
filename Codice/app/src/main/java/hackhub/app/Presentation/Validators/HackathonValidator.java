package hackhub.app.Presentation.Validators;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import hackhub.app.Application.Requests.CreaHackathonRequest;

@Component
public class HackathonValidator {

    public List<String> validateCreation(CreaHackathonRequest request) {
        List<String> errors = new ArrayList<>();

        if (request == null) {
            errors.add("La richiesta non può essere nulla.");
            return errors;
        }

        // Validazione Campi Obbligatori
        if (request.getNome() == null || request.getNome().trim().isEmpty()) {
            errors.add("Il campo Nome è obbligatorio.");
        }
        if (request.getIdGiudice() == null || request.getIdGiudice().trim().isEmpty()) {
            errors.add("ID Giudice obbligatorio.");
        }
        if (request.getIdMentori() == null || request.getIdMentori().isEmpty()) {
            errors.add("Deve essere presente almeno un mentore.");
        }

        validateCommon(request, errors);

        return errors;
    }

    private void validateCommon(CreaHackathonRequest request, List<String> errors) {
        // Validazione Numerica
        if (request.getPremioInDenaro() < 0) {
            errors.add("Il premio in denaro non può essere negativo.");
        }

        // Validazione Date Completa
        if (request.getInizioIscrizioni() == null || request.getScadenzaIscrizioni() == null ||
                request.getDataInizio() == null || request.getDataFine() == null) {
            errors.add("Tutte le date sono obbligatorie (Inizio Iscrizioni, Scadenza, Inizio Evento, Fine Evento).");
        } else {
            // 1. Controllo Durata Evento: Fine deve essere dopo Inizio
            if (!request.getDataFine().isAfter(request.getDataInizio())) {
                errors.add("La data di fine evento deve essere successiva alla data di inizio.");
            }
            // 2. Controllo Finestra Iscrizioni: Scadenza deve essere dopo Inizio Iscrizioni
            if (!request.getScadenzaIscrizioni().isAfter(request.getInizioIscrizioni())) {
                errors.add("La scadenza delle iscrizioni deve essere successiva all'apertura delle iscrizioni.");
            }
            // 3. Controllo Chiusura Iscrizioni vs Evento: Le iscrizioni devono chiudere
            // prima che l'evento inizi
            if (request.getScadenzaIscrizioni().isAfter(request.getDataInizio())) {
                errors.add("Le iscrizioni devono chiudersi prima dell'inizio dell'hackathon.");
            }
            // 4. Controllo Coerenza Globale: Le iscrizioni non possono aprirsi dopo che
            // l'evento è iniziato
            if (request.getInizioIscrizioni().isAfter(request.getDataInizio())) {
                errors.add("L'inizio delle iscrizioni non può essere successivo all'inizio dell'evento.");
            }
            // 5. Controllo Date Passate: Le iscrizioni non possono iniziare nel passato
            if (request.getInizioIscrizioni().isBefore(LocalDateTime.now())) {
                errors.add("La data di inizio iscrizioni non può essere nel passato.");
            }
        }
    }
}
