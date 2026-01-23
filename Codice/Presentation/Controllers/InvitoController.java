package Presentation.Controllers;

import Application.Requests.CreaInvitoRequest;
import Application.Requests.RispostaInvitoRequest;
import Application.Services.InvitoService;
import Core.POJO_Entities.Invito;


public class InvitoController {
    private final InvitoService invitoService;

    public InvitoController(InvitoService invitoService) {
        this.invitoService = invitoService;
    }

    // METODO ACCESSIBILE SOLO DA UN LEADER DEL TEAM O UN MEMBRO DEL TEAM
    public Object inviaInvito(CreaInvitoRequest request) {
        // Validazione Input (potrebbe servire un nuovo validator o semplice null check)
        if (request.getTeamId() == null || request.getUserDestinatarioId() == null
                || request.getUserMittenteId() == null
                || request.getTeamId().isEmpty() || request.getUserDestinatarioId().isEmpty()
                || request.getUserMittenteId().isEmpty()) {
            return "Errore 400: Validazione fallita -> ID mancanti";
        }

        try {
            // 2. Chiamata al Business Service
            Invito invito = invitoService.inviaInvito(request);
            return "Successo 200: Invito inviato con ID " + invito.getId();

        } catch (Exception e) {
            // Gestione errori di dominio (es. Utente non trovato, Ruoli errati)
            return "Errore 500: " + e.getMessage();
        }
    }

    // METODO ACCESSIBILE SOLO DA UN UTENTE NON MEMBRO DEL TEAM
    public Object gestisciRispostaInvito(RispostaInvitoRequest rispostaInvitoRequest) {
        // Validazione Input (potrebbe servire un nuovo validator o semplice null check)
        if (rispostaInvitoRequest.getInvitoId() == null || rispostaInvitoRequest.isAccettato() == null
        || rispostaInvitoRequest.getUserId() == null || rispostaInvitoRequest.getInvitoId().isEmpty() 
        || rispostaInvitoRequest.getUserId().isEmpty()) {
            return "Errore 400: Validazione fallita -> ID mancanti";
        }
        try {
            // 2. Chiamata al Business Service
            invitoService.gestisciRisposta(rispostaInvitoRequest);
            return "Successo 200: Invito accettato con ID " + rispostaInvitoRequest.getInvitoId();

        } catch (Exception e) {
            // Gestione errori di dominio (es. Utente non trovato, Ruoli errati)
            return "Errore 500: " + e.getMessage();
        }
    }

}
