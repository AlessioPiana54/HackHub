package hackhub.app.Presentation.Controllers;

import hackhub.app.Application.DTOs.UserDTO;
import hackhub.app.Application.Requests.LoginRequest;
import hackhub.app.Application.Requests.RegisterRequest;
import hackhub.app.Application.Services.AuthService;
import hackhub.app.Application.Services.UserService;
import hackhub.app.Application.Utils.ISessionManager;
import hackhub.app.Core.POJO_Entities.User;
import hackhub.app.Presentation.Validators.AuthValidator;
import java.util.Collections;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller per la gestione dell'autenticazione utente.
 * Gestisce registrazione, login e logout.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController extends AbstractController {

  private final AuthService authService;
  private final AuthValidator authValidator;
  private final UserService userService;

  public AuthController(
    AuthService authService,
    AuthValidator authValidator,
    UserService userService,
    ISessionManager sessionManager
  ) {
    super(sessionManager);
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
  public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
    validateRequest(authValidator.validateRegister(request));
    authService.register(request);

    // Dopo la registrazione, facciamo login automatico e restituiamo il token
    String token = authService.login(
      new LoginRequest(request.getEmail(), request.getPassword())
    );

    // Ottieni i dati dell'utente
    User user = getAuthenticatedUser(token);
    UserDTO userDTO = userService.getUserProfile(user.getId());

    // Crea risposta con token e dati utente
    java.util.Map<String, Object> response = new java.util.HashMap<>();
    response.put("token", token);
    response.put("user", userDTO);

    return ResponseEntity.ok(response);
  }

  /**
   * Effettua il login di un utente.
   *
   * @param request La richiesta di login contenente email e password.
   * @return Una ResponseEntity con un token di autenticazione o errori di
   *         validazione.
   */
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    validateRequest(authValidator.validateLogin(request));
    String token = authService.login(request);

    // Ottieni i dati dell'utente
    User user = getAuthenticatedUser(token);
    UserDTO userDTO = userService.getUserProfile(user.getId());

    // Crea risposta con token e dati utente
    java.util.Map<String, Object> response = new java.util.HashMap<>();
    response.put("token", token);
    response.put("user", userDTO);

    return ResponseEntity.ok(response);
  }

  /**
   * Effettua il logout di un utente invalidando il token.
   *
   * @param token Il token di autorizzazione dell'utente.
   * @return Una ResponseEntity con un messaggio di conferma o errore se il token
   *         è mancante.
   */
  @PostMapping("/logout")
  public ResponseEntity<?> logout(
    @RequestHeader("Authorization") String token
  ) {
    if (token != null && !token.isEmpty()) {
      authService.logout(token);

      // Restituisci risposta JSON invece di testo
      java.util.Map<String, String> response = new java.util.HashMap<>();
      response.put("message", "Logout effettuato con successo.");
      return ResponseEntity.ok(response);
    }

    // Restituisci errore JSON invece di testo
    java.util.Map<String, String> errorResponse = new java.util.HashMap<>();
    errorResponse.put("error", "Token mancante.");
    return ResponseEntity.badRequest().body(errorResponse);
  }
}
