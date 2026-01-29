package hackhub.app.Presentation.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import hackhub.app.Application.Requests.LoginRequest;
import hackhub.app.Application.Requests.RegisterRequest;
import hackhub.app.Application.Services.AuthService;
import hackhub.app.Presentation.Validators.AuthValidator;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthValidator authValidator;

    @Autowired
    public AuthController(AuthService authService, AuthValidator authValidator) {
        this.authService = authService;
        this.authValidator = authValidator;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        List<String> errors = authValidator.validateRegister(request);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body("Errore Validazione: " + String.join(", ", errors));
        }

        authService.register(request);
        return ResponseEntity.ok("Registrazione effettuata con successo.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        List<String> errors = authValidator.validateLogin(request);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body("Errore Validazione: " + String.join(", ", errors));
        }

        String token = authService.login(request);
        return ResponseEntity.ok(java.util.Collections.singletonMap("token", token));
    }
}
