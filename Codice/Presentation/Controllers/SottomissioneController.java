package Presentation.Controllers;

import Application.Requests.CreaValutazioneRequest;
import Application.Requests.InviaSottomissioneRequest;
import Application.Services.SottomissioneService;
import Core.POJO_Entities.Sottomissione;
import Presentation.Validators.SottomissioneValidator;
import Presentation.Validators.ValutazioneValidator;
import java.util.List;

public class SottomissioneController {
    private final SottomissioneService sottomissioneService;
    private final SottomissioneValidator sottomissioneValidator;
    private final ValutazioneValidator valutazioneValidator;

    public SottomissioneController(SottomissioneService sottomissioneService,
            SottomissioneValidator sottomissioneValidator,
            ValutazioneValidator valutazioneValidator) {
        this.sottomissioneService = sottomissioneService;
        this.sottomissioneValidator = sottomissioneValidator;
        this.valutazioneValidator = valutazioneValidator;
    }

    // METODO ACCESSIBILE SOLO DA UN LEADER O DA UN MEMBRO DEL TEAM
    public Object inviaSottomissione(InviaSottomissioneRequest request) {
        // 1. Validazione Input
        List<String> validationErrors = sottomissioneValidator.validateCreation(request);
        if (!validationErrors.isEmpty()) {
            return "Errore 400: Validazione fallita -> " + String.join(", ", validationErrors);
        }

        try {
            // 2. Chiamata al Service
            Sottomissione sottomissione = sottomissioneService.inviaSottomissione(request);
            return "Successo 200: Sottomissione inviata con ID " + sottomissione.getId();

        } catch (Exception e) {
            return "Errore 500: " + e.getMessage();
        }
    }

    // METODO ACCESSIBILE SOLO DA UN GIUDICE
    public Object valutaSottomissione(CreaValutazioneRequest request) {
        // 1. Validazione Input
        List<String> validationErrors = valutazioneValidator.validateCreation(request);
        if (!validationErrors.isEmpty()) {
            return "Errore 400: Validazione fallita -> " + String.join(", ", validationErrors);
        }

        try {
            // 2. Chiamata al Service
            Sottomissione sottomissione = sottomissioneService.valutaSottomissione(request);
            return "Successo 200: Sottomissione " + sottomissione.getId() + " valutata con voto " + request.getVoto();

        } catch (Exception e) {
            return "Errore 500: " + e.getMessage();
        }
    }
}
