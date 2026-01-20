package Presentation.Controllers;

import Application.Requests.CreaHackathonRequest;
import Application.Services.HackathonService;
import Core.POJO_Entities.Hackathon;
import Presentation.Validators.CreaHackathonValidator;

import java.util.List;

public class HackathonController {

    private final HackathonService hackathonService;
    private final CreaHackathonValidator creaHackathonValidator;

    public HackathonController(HackathonService hackathonService, CreaHackathonValidator creaHackathonValidator) {
        this.hackathonService = hackathonService;
        this.creaHackathonValidator = creaHackathonValidator;
    }

    // METODO ACCESSIBILE SOLO DA UN ORGANIZZATORE
    public Object creaHackathon(CreaHackathonRequest request) {
        // 1. Validazione Input (Validation Pipeline)
        List<String> validationErrors = creaHackathonValidator.validate(request);
        if (!validationErrors.isEmpty()) {
            // Ritorna un errore 400 Bad Request simulato
            return "Errore 400: Validazione fallita -> " + String.join(", ", validationErrors);
        }

        try {
            // 2. Chiamata al Business Service
            Hackathon hackathon = hackathonService.creaHackathon(request);
            
            // 3. Risposta 200 OK (Mappata a DTO se necessario, qui ritorno l'oggetto per semplicità)
            return "Successo 200: Hackathon creato con ID " + hackathon.getId();
            
        } catch (Exception e) {
            // Gestione errori di dominio (es. Utente non trovato, Ruoli errati)
            return "Errore 500: " + e.getMessage();
        }
    }
}
