package hackhub.app.Presentation.Controllers;

import java.util.Collections;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import hackhub.app.Application.Requests.LoginRequest;
import hackhub.app.Application.Requests.RegisterRequest;
import hackhub.app.Application.Services.AuthService;
import hackhub.app.Application.Utils.ISessionManager;
import hackhub.app.Presentation.Validators.AuthValidator;

/**
 * Controller per la gestione dell'autenticazione utente.
 * Gestisce registrazione, login e logout.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController extends AbstractController {

    private final AuthService authService;
    private final AuthValidator authValidator;

    public AuthController(AuthService authService, AuthValidator authValidator, ISessionManager sessionManager) {
        super(sessionManager);
        this.authService = authService;
        this.authValidator = authValidator;
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
        return ResponseEntity.ok("Registrazione effettuata con successo.");
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
        return ResponseEntity.ok(Collections.singletonMap("token", token));
    }

    /**
     * Effettua il logout di un utente invalidando il token.
     *
     * @param token Il token di autorizzazione dell'utente.
     * @return Una ResponseEntity con un messaggio di conferma o errore se il token
     *         è mancante.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        if (token != null && !token.isEmpty()) {
            authService.logout(token);
            return ResponseEntity.ok("Logout effettuato con successo.");
        }
        return ResponseEntity.badRequest().body("Token mancante.");
    }
}
