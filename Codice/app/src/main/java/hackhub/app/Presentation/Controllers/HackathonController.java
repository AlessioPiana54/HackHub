package hackhub.app.Presentation.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import hackhub.app.Application.Requests.CreaHackathonRequest;
import hackhub.app.Application.Services.HackathonService;
import hackhub.app.Application.Utils.ISessionManager;
import hackhub.app.Core.POJO_Entities.Hackathon;
import hackhub.app.Core.POJO_Entities.User;
import hackhub.app.Presentation.Validators.HackathonValidator;
import hackhub.app.Application.DTOs.HackathonSummaryDTO;
import java.util.List;

/**
 * Controller per la gestione degli Hackathon.
 */
@RestController
@RequestMapping("/api/hackathons")
public class HackathonController extends AbstractController {

    private final HackathonService hackathonService;
    private final HackathonValidator hackathonValidator;

    public HackathonController(HackathonService hackathonService, HackathonValidator hackathonValidator,
            ISessionManager sessionManager) {
        super(sessionManager);
        this.hackathonService = hackathonService;
        this.hackathonValidator = hackathonValidator;
    }

    /**
     * Recupera la lista degli hackathon pubblici.
     *
     * @return Una lista di Hackathon.
     */
    @GetMapping("")
    public ResponseEntity<List<HackathonSummaryDTO>> getHackathons() {
        return ResponseEntity.ok(hackathonService.getPublicHackathons());
    }

    /**
     * Crea un nuovo hackathon.
     *
     * @param token   Il token di autorizzazione dell'organizzatore.
     * @param request I dati per la creazione dell'hackathon.
     * @return L'hackathon creato o un errore di validazione.
     */
    @PostMapping("/crea")
    public ResponseEntity<?> creaHackathon(@RequestHeader("Authorization") String token,
            @RequestBody CreaHackathonRequest request) {
        User user = getAuthenticatedUser(token);
        validateRequest(hackathonValidator.validateCreation(request));
        Hackathon hackathon = hackathonService.creaHackathon(request, user.getId());
        return ResponseEntity.ok(hackathon);
    }

    /**
     * Termina la fase di valutazione di un hackathon.
     *
     * @param hackathonId L'ID dell'hackathon.
     * @param token       Il token di autorizzazione.
     * @return Un messaggio di conferma.
     */
    @PostMapping("/{hackathonId}/terminaValutazione")
    public ResponseEntity<String> terminaFaseValutazione(@PathVariable String hackathonId,
            @RequestHeader("Authorization") String token) {
        User user = getAuthenticatedUser(token);
        validateIds(hackathonId);
        hackathonService.terminaFaseValutazione(hackathonId, user.getId());
        return ResponseEntity.ok("Fase di valutazione terminata. Hackathon ora in fase di premiazione.");
    }

    /**
     * Recupera la classifica provvisoria o definitiva di un hackathon.
     *
     * @param hackathonId L'ID dell'hackathon.
     * @param token       Il token di autorizzazione.
     * @return La classifica dei team.
     */
    @GetMapping("/{hackathonId}/classifica")
    public ResponseEntity<?> getClassifica(@PathVariable String hackathonId,
            @RequestHeader("Authorization") String token) {
        User user = getAuthenticatedUser(token);
        validateIds(hackathonId);
        return ResponseEntity.ok(hackathonService.getClassifica(hackathonId, user.getId()));
    }

    /**
     * Proclama il vincitore di un hackathon.
     *
     * @param hackathonId L'ID dell'hackathon.
     * @param teamId      L'ID del team vincitore.
     * @param token       Il token di autorizzazione.
     * @return Un messaggio di conferma.
     */
    @PostMapping("/{hackathonId}/vincitore")
    public ResponseEntity<String> proclamaVincitore(@PathVariable String hackathonId, @RequestParam String teamId,
            @RequestHeader("Authorization") String token) {
        User user = getAuthenticatedUser(token);
        validateIds(hackathonId, teamId);
        hackathonService.proclamaVincitore(hackathonId, teamId, user.getId());
        return ResponseEntity.ok("Vincitore proclamato e Hackathon concluso.");
    }
}
