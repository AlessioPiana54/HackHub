package hackhub.app.Presentation.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import hackhub.app.Application.Requests.CreaSegnalazioneRequest;
import hackhub.app.Application.Services.SegnalazioneService;
import hackhub.app.Core.POJO_Entities.Segnalazione;
import hackhub.app.Application.Utils.ISessionManager;
import hackhub.app.Core.POJO_Entities.User;
import hackhub.app.Presentation.Validators.SegnalazioneValidator;
import hackhub.app.Application.DTOs.SegnalazioneDTO;
import java.util.List;

/**
 * Controller per la gestione delle segnalazioni.
 */
@RestController
@RequestMapping("/api/segnalazioni")
public class SegnalazioneController extends AbstractController {
    private final SegnalazioneService service;
    private final SegnalazioneValidator validator;

    public SegnalazioneController(SegnalazioneService service, SegnalazioneValidator validator,
            ISessionManager sessionManager) {
        super(sessionManager);
        this.service = service;
        this.validator = validator;
    }

    /**
   * Crea una nuova segnalazione per un team o un progetto.
   *
   * @param token   il token di sessione dell'utente autenticato
   * @param request i dati per la creazione della segnalazione
   * @return la segnalazione creata
   */
  @PostMapping("")
  public ResponseEntity<Segnalazione> creaSegnalazione(
    @RequestHeader("Authorization") String token,
    @RequestBody CreaSegnalazioneRequest request
  ) {
        User user = getAuthenticatedUser(token);
        validateRequest(validator.validateCreation(request));
        Segnalazione segnalazione = service.creaSegnalazione(request, user.getId());
        return ResponseEntity.ok(segnalazione);
    }

    /**
     * Recupera le segnalazioni relative a un hackathon.
     *
     * @param hackathonId L'ID dell'hackathon.
     * @param token       Il token di autorizzazione dell'organizzatore.
     * @return Una lista di Segnalazioni.
     */
    @GetMapping
    public ResponseEntity<?> getSegnalazioni(@RequestParam String hackathonId,
            @RequestHeader("Authorization") String token) {
        User user = getAuthenticatedUser(token);
        validateIds(hackathonId);
        List<SegnalazioneDTO> segnalazioni = service.getSegnalazioni(hackathonId, user.getId());
        return ResponseEntity.ok(segnalazioni);
    }
}
