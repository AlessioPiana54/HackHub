package hackhub.app.Presentation.Controllers;

import hackhub.app.Application.DTOs.UserDTO;
import hackhub.app.Application.Requests.UpdateProfileRequest;
import hackhub.app.Application.Services.Interfaces.IUserService;
import hackhub.app.Application.IUnitOfWork.IUnitOfWork;
import hackhub.app.Application.Utils.IJwtService;
import hackhub.app.Core.POJO_Entities.User;
import hackhub.app.Core.Enums.Ruolo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controller per la gestione dei profili utente.
 */
@RestController
@RequestMapping("/api/users")
public class UserController extends AbstractController {

  private final IUserService userService;

  public UserController(
    IUserService userService,
    IJwtService jwtService,
    IUnitOfWork unitOfWork
  ) {
    super(jwtService, unitOfWork);
    this.userService = userService;
  }

  private static final Logger logger = LoggerFactory.getLogger(UserController.class);

  /**
   * Recupera i dati dell'utente autenticato.
   *
   * @param token Il token di autorizzazione dell'utente.
   * @return I dati dell'utente autenticato.
   */
  @GetMapping("/me")
  public ResponseEntity<UserDTO> getProfile(
    @RequestHeader("Authorization") String token
  ) {
    User user = getAuthenticatedUser(token);
    UserDTO userDTO = userService.getUserProfile(user.getId());
    return ResponseEntity.ok(userDTO);
  }

  /**
   * Aggiorna i dati dell'utente autenticato.
   *
   * @param token Il token di autorizzazione dell'utente.
   * @param request I dati aggiornati dell'utente.
   * @return I dati aggiornati dell'utente.
   */
  @PutMapping("/me")
  public ResponseEntity<UserDTO> updateProfile(
    @RequestHeader("Authorization") String token,
    @RequestBody UpdateProfileRequest request
  ) {
    User user = getAuthenticatedUser(token);
    UserDTO userDTO = userService.updateProfile(user.getId(), request);
    return ResponseEntity.ok(userDTO);
  }

  /**
   * Recupera gli utenti in base al ruolo specificato.
   * 
   * Serve all'organizzatore per scegliere giudice e mentori di un nuovo hackathon:
   * è consentito solo agli ORGANIZZATORI e solo per i ruoli GIUDICE e MENTORE.
   *
   * @param ruolo Il ruolo degli utenti (GIUDICE o MENTORE)
   * @return Lista di utenti con quel ruolo
   */
  @GetMapping("/by-role/{ruolo}")
  public ResponseEntity<List<UserDTO>> getUsersByRole(
    @RequestHeader("Authorization") String token,
    @PathVariable Ruolo ruolo
  ) {
    User user = getAuthenticatedUser(token);
    validateUserRole(user, Ruolo.ORGANIZZATORE, "Solo gli organizzatori possono consultare l'elenco di giudici e mentori.");
    if (ruolo != Ruolo.GIUDICE && ruolo != Ruolo.MENTORE) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Errore Validazione: sono consultabili solo i ruoli GIUDICE e MENTORE.");
    }
    List<UserDTO> users = userService.getUsersByRuolo(ruolo);
    logger.debug("Trovati {} utenti con ruolo {}", users.size(), ruolo);
    return ResponseEntity.ok(users);
  }
}
