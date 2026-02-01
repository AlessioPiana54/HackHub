package hackhub.app.Presentation.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import hackhub.app.Application.Requests.CreaValutazioneRequest;
import hackhub.app.Application.Requests.InviaSottomissioneRequest;
import hackhub.app.Application.Requests.ModificaSottomissioneRequest;
import hackhub.app.Application.Services.SottomissioneService;
import hackhub.app.Core.POJO_Entities.Sottomissione;
import hackhub.app.Application.Utils.ISessionManager;
import hackhub.app.Core.POJO_Entities.User;
import hackhub.app.Core.POJO_Entities.Valutazione;
import hackhub.app.Presentation.Validators.SottomissioneValidator;
import hackhub.app.Presentation.Validators.ValutazioneValidator;

/**
 * Controller per la gestione delle sottomissioni.
 */
@RestController
@RequestMapping("/api/sottomissioni")
public class SottomissioneController extends AbstractController {

    private final SottomissioneService sottomissioneService;
    private final SottomissioneValidator sottomissioneValidator;
    private final ValutazioneValidator valutazioneValidator;

    public SottomissioneController(SottomissioneService sottomissioneService,
            SottomissioneValidator sottomissioneValidator,
            ValutazioneValidator valutazioneValidator,
            ISessionManager sessionManager) {
        super(sessionManager);
        this.sottomissioneService = sottomissioneService;
        this.sottomissioneValidator = sottomissioneValidator;
        this.valutazioneValidator = valutazioneValidator;
    }

    /**
     * Invia una nuova sottomissione.
     *
     * @param token   Il token di autorizzazione dell'utente.
     * @param request I dati della sottomissione.
     * @return La sottomissione creata o un errore di validazione.
     */
    @PostMapping("/invia")
    public ResponseEntity<?> inviaSottomissione(@RequestHeader("Authorization") String token,
            @RequestBody InviaSottomissioneRequest request) {
        User user = getAuthenticatedUser(token);
        validateRequest(sottomissioneValidator.validateCreation(request));
        Sottomissione sottomissione = sottomissioneService.inviaSottomissione(request, user.getId());
        return ResponseEntity.ok(sottomissione);
    }

    /**
     * Modifica una sottomissione esistente.
     *
     * @param token   Il token di autorizzazione dell'utente.
     * @param request I dati aggiornati della sottomissione.
     * @return La sottomissione modificata o un errore di validazione.
     */
    @PutMapping("/modifica")
    public ResponseEntity<?> modificaSottomissione(@RequestHeader("Authorization") String token,
            @RequestBody ModificaSottomissioneRequest request) {
        User user = getAuthenticatedUser(token);
        validateRequest(sottomissioneValidator.validateModification(request));
        Sottomissione sottomissione = sottomissioneService.modificaSottomissione(request, user.getId());
        return ResponseEntity.ok(sottomissione);
    }

    /**
     * Valuta una sottomissione.
     *
     * @param token   Il token di autorizzazione del giudice.
     * @param request I dati della valutazione.
     * @return La valutazione creata o un errore di validazione.
     */
    @PostMapping("/valuta")
    public ResponseEntity<?> valutaSottomissione(@RequestHeader("Authorization") String token,
            @RequestBody CreaValutazioneRequest request) {
        User user = getAuthenticatedUser(token);
        validateRequest(valutazioneValidator.validateCreation(request));
        Valutazione valutazione = sottomissioneService.valutaSottomissione(request, user.getId());
        return ResponseEntity.ok(valutazione);
    }
}
