package hackhub.app.Presentation.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import hackhub.app.Application.Requests.CreaRichiestaSupportoRequest;
import hackhub.app.Application.Requests.ProponiCallRequest;
import hackhub.app.Application.Services.RichiestaSupportoService;
import hackhub.app.Core.POJO_Entities.RichiestaSupporto;
import hackhub.app.Application.Utils.ISessionManager;
import hackhub.app.Core.POJO_Entities.User;
import hackhub.app.Presentation.Validators.RichiestaSupportoValidator;
import hackhub.app.Application.DTOs.RichiestaSupportoDTO;
import java.util.List;

/**
 * Controller per la gestione delle richieste di supporto.
 */
@RestController
@RequestMapping("/api/supporto")
public class RichiestaSupportoController extends AbstractController {

    private final RichiestaSupportoService supportoService;
    private final RichiestaSupportoValidator validator;

    public RichiestaSupportoController(RichiestaSupportoService supportoService, RichiestaSupportoValidator validator,
            ISessionManager sessionManager) {
        super(sessionManager);
        this.supportoService = supportoService;
        this.validator = validator;
    }

    /**
     * Crea una nuova richiesta di supporto.
     *
     * @param token   Il token di autorizzazione del richiedente.
     * @param request I dati della richiesta di supporto.
     * @return La richiesta creata o un errore di validazione.
     */
    @PostMapping("/crea")
    public ResponseEntity<?> creaRichiesta(@RequestHeader("Authorization") String token,
            @RequestBody CreaRichiestaSupportoRequest request) {
        User user = getAuthenticatedUser(token);
        validateRequest(validator.validateCreation(request));
        RichiestaSupporto richiesta = supportoService.creaRichiesta(request, user.getId());
        return ResponseEntity.ok(richiesta);
    }

    /**
     * Recupera le richieste di supporto pertinenti a un mentore per un dato
     * hackathon.
     *
     * @param hackathonId L'ID dell'hackathon.
     * @param token       Il token di autorizzazione del mentore.
     * @return Una lista di Richieste di Supporto.
     */
    @GetMapping("/mentore")
    public ResponseEntity<?> getRichiestePerMentore(@RequestParam String hackathonId,
            @RequestHeader("Authorization") String token) {
        User user = getAuthenticatedUser(token);
        validateIds(hackathonId);
        List<RichiestaSupportoDTO> richieste = supportoService.getRichiestePerMentore(hackathonId, user.getId());
        return ResponseEntity.ok(richieste);
    }

    /**
     * Propone una call per una richiesta di supporto.
     *
     * @param token   Il token di autorizzazione del mentore.
     * @param request I dati per la proposta della call.
     * @return La richiesta aggiornata con la proposta di call.
     */
    @PostMapping("/proponi-call")
    public ResponseEntity<?> proponiCall(@RequestHeader("Authorization") String token,
            @RequestBody ProponiCallRequest request) {
        User user = getAuthenticatedUser(token);
        validateRequest(validator.validateProponiCall(request));
        RichiestaSupporto richiesta = supportoService.proponiCall(request, user.getId());
        return ResponseEntity.ok(richiesta);
    }

    /**
     * Visualizza le proposte di call gestite per un team specifico in un hackathon.
     *
     * @param hackathonId L'ID dell'hackathon.
     * @param teamId      L'ID del team.
     * @param token       Il token di autorizzazione.
     * @return Una lista di Richieste di Supporto.
     */
    @GetMapping("/visualizza-proposte-call")
    public ResponseEntity<?> getRichiesteGestitePerTeam(@RequestParam String hackathonId, @RequestParam String teamId,
            @RequestHeader("Authorization") String token) {
        User user = getAuthenticatedUser(token);
        validateIds(hackathonId, teamId);
        List<RichiestaSupportoDTO> richieste = supportoService.getRichiesteGestitePerTeam(hackathonId, teamId,
                user.getId());
        return ResponseEntity.ok(richieste);
    }
}
