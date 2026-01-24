package Presentation.Controllers;

import Application.Requests.CreaRichiestaSupportoRequest;
import Application.Services.RichiestaSupportoService;
import Presentation.Validators.RichiestaSupportoValidator;
import Core.POJO_Entities.RichiestaSupporto;

import java.util.List;

public class RichiestaSupportoController {
    private final RichiestaSupportoService service;
    private final RichiestaSupportoValidator validator;

    public RichiestaSupportoController(RichiestaSupportoService service, RichiestaSupportoValidator validator) {
        this.service = service;
        this.validator = validator;
    }

    // METODO ACCESSIBILE SOLO DA UN MEMBRO DEL TEAM E DA LEADER DEL TEAM
    public Object creaRichiestaSupporto(CreaRichiestaSupportoRequest request) {
        // Validazione Input
        List<String> errors = validator.validateCreation(request);
        if (!errors.isEmpty()) {
            return "Errore 400: " + String.join(", ", errors);
        }
        try {
            RichiestaSupporto rich = service.creaRichiesta(request);
            return "Successo 200: Richiesta di supporto creata con ID " + rich.getId();
        } catch (Exception e) {
            return "Errore 500: " + e.getMessage();
        }
    }
}
