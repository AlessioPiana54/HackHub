package hackhub.app.Presentation.Controllers;

import hackhub.app.Application.DTOs.AuthResponse;
import hackhub.app.Application.DTOs.MessageResponse;
import hackhub.app.Application.DTOs.UserDTO;
import hackhub.app.Application.Requests.LoginRequest;
import hackhub.app.Application.Requests.RegisterRequest;
import hackhub.app.Application.Services.Interfaces.IAuthService;
import hackhub.app.Application.Services.Interfaces.IUserService;
import hackhub.app.Application.IUnitOfWork.IUnitOfWork;
import hackhub.app.Application.Utils.IJwtService;
import hackhub.app.Core.POJO_Entities.User;
import hackhub.app.Presentation.Validators.AuthValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller per la gestione dell'autenticazione utente.
 * Gestisce registrazione, login e logout.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController extends AbstractController {

  private final IAuthService authService;
  private final AuthValidator authValidator;
  private final IUserService userService;

  public AuthController(
    IAuthService authService,
    AuthValidator authValidator,
    IUserService userService,
    IJwtService jwtService,
    IUnitOfWork unitOfWork
  ) {
    super(jwtService, unitOfWork);
    this.authService = authService;
    this.authValidator = authValidator;
    this.userService = userService;
  }

  /**
   * Registra un nuovo utente.
   *
   * @param request La richiesta di registrazione contenente i dati dell'utente.
   * @return Una ResponseEntity con un messaggio di successo o errori di
   *         validazione.
   */
  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
    validateRequest(authValidator.validateRegister(request));
    authService.register(request);

    // Dopo la registrazione, facciamo login automatico e restituiamo il token
    String token = authService.login(
      new LoginRequest(request.getEmail(), request.getPassword())
    );

    // Ottieni i dati dell'utente
    User user = getAuthenticatedUser(token);
    UserDTO userDTO = userService.getUserProfile(user.getId());

    return ResponseEntity.ok(new AuthResponse(token, userDTO));
  }

  /**
   * Effettua il login di un utente.
   *
   * @param request La richiesta di login contenente email e password.
   * @return Una ResponseEntity con un token di autenticazione o errori di
   *         validazione.
   */
  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
    validateRequest(authValidator.validateLogin(request));
    String token = authService.login(request);

    // Ottieni i dati dell'utente
    User user = getAuthenticatedUser(token);
    UserDTO userDTO = userService.getUserProfile(user.getId());

    return ResponseEntity.ok(new AuthResponse(token, userDTO));
  }

  /**
   * Effettua il logout di un utente invalidando il token.
   *
   * @param token Il token di autorizzazione dell'utente.
   * @return Una ResponseEntity con un messaggio di conferma o errore se il token
   *         è mancante.
   */
  @PostMapping("/logout")
  public ResponseEntity<MessageResponse> logout(
    @RequestHeader("Authorization") String token
  ) {
    if (token != null && !token.isEmpty()) {
      authService.logout(token);
      return ResponseEntity.ok(new MessageResponse("Logout effettuato con successo."));
    }

    return ResponseEntity.badRequest().body(new MessageResponse("Token mancante."));
  }
}
