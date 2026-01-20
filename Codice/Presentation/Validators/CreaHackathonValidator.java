package Presentation.Validators;

import java.util.ArrayList;
import java.util.List;

import Application.Requests.CreaHackathonRequest;

public class CreaHackathonValidator implements IValidator<CreaHackathonRequest> {

    @Override
    public List<String> validate(CreaHackathonRequest request) {
        List<String> errors = new ArrayList<>();

        // 1. Controllo difensivo se l'oggetto richiesta è nullo
        if (request == null) {
            errors.add("La richiesta non può essere nulla.");
            return errors; // Interrompiamo qui perché non possiamo accedere ai campi
        }

        // Validazione Campi Obbligatori
        if (request.getNome() == null || request.getNome().trim().isEmpty()) {
            errors.add("Il campo Nome è obbligatorio.");
        }
        if (request.getIdOrganizzatore() == null || request.getIdOrganizzatore().trim().isEmpty()) {
            errors.add("ID Organizzatore obbligatorio.");
        }
        if (request.getIdGiudice() == null || request.getIdGiudice().trim().isEmpty()) {
            errors.add("ID Giudice obbligatorio.");
        }

        // Validazione Numerica
        if (request.getPremioInDenaro() < 0) {
            errors.add("Il premio in denaro non può essere negativo.");
        }
        if (request.getDimensioneMaxTeam() <= 0) {
            errors.add("La dimensione del team deve essere maggiore di zero.");
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
            // (Questo copre casi limite in cui le date sono completamente sballate)
            if (request.getInizioIscrizioni().isAfter(request.getDataInizio())) {
                errors.add("L'inizio delle iscrizioni non può essere successivo all'inizio dell'evento.");
            }
        }

        return errors;
    }
}
