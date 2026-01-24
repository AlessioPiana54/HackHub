package Presentation.Controllers;

import java.util.List;
import Application.Requests.CreaSegnalazioneRequest;
import Application.Services.SegnalazioneService;
import Core.POJO_Entities.Segnalazione;
import Presentation.Validators.SegnalazioneValidator;

public class SegnalazioneController {
    private final SegnalazioneService service;
    private final SegnalazioneValidator validator;

    public SegnalazioneController(SegnalazioneService service, SegnalazioneValidator validator) {
        this.service = service;
        this.validator = validator;
    }

    // METODO ACCESSIBILE SOLO DA UN MENTORE
    public Object creaSegnalazione(CreaSegnalazioneRequest request) {
        List<String> errors = validator.validateCreation(request);
        if (!errors.isEmpty()) {
            return "Errore 400: Validazione fallita -> " + String.join(", ", errors);
        }

        try {
            Segnalazione s = service.creaSegnallazione(request);
            return "Successo 200: Segnalazione creata con ID " + s.getId();
        } catch (Exception e) {
            return "Errore 500: " + e.getMessage();
        }
    }

    // METODO ACCESSIBILE SOLO DA UN ORGANIZZATORE
    public Object getSegnalazioni(String idHackathon, String idOrganizer) {
        if (idHackathon == null || idHackathon.trim().isEmpty() || idOrganizer == null
                || idOrganizer.trim().isEmpty()) {
            return "Errore 400: ID Hackathon o ID Organizzatore mancanti.";
        }

        try {
            List<Segnalazione> report = service.getSegnalazioni(idHackathon, idOrganizer);
            return report;
        } catch (Exception e) {
            return "Errore 500: " + e.getMessage();
        }
    }
}
