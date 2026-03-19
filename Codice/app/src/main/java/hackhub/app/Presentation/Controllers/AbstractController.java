package hackhub.app.Presentation.Controllers;

import hackhub.app.Application.IUnitOfWork.IUnitOfWork;
import hackhub.app.Application.Utils.IJwtService;
import hackhub.app.Core.POJO_Entities.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

/**
 * Controller di base che fornisce funzionalità comuni per autenticazione e
 * validazione.
 */
public abstract class AbstractController {

    protected final IJwtService jwtService;
    protected final IUnitOfWork unitOfWork;

    public AbstractController(IJwtService jwtService, IUnitOfWork unitOfWork) {
        this.jwtService = jwtService;
        this.unitOfWork = unitOfWork;
    }

    /**
     * Recupera l'utente autenticato dal token.
     *
     * @param token Il token di autorizzazione.
     * @return L'utente autenticato.
     * @throws ResponseStatusException con stato 401 se l'utente non è autenticato.
     */
    protected User getAuthenticatedUser(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (token == null || token.isBlank() || !jwtService.isTokenValid(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utente non autenticato.");
        }
        String userId = jwtService.extractUserId(token);
        Optional<User> user = unitOfWork.userRepository().findById(userId);
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utente non autenticato.");
        }
        return user.get();
    }

    /**
     * Verifica la presenza di errori di validazione.
     *
     * @param errors La lista di errori di validazione.
     * @throws ResponseStatusException con stato 400 se la lista errori non è vuota.
     */
    protected void validateRequest(List<String> errors) {
        if (!errors.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Errore Validazione: " + String.join(", ", errors));
        }
    }

    /**
     * Verifica che gli ID passati non siano nulli o vuoti.
     *
     * @param ids Gli ID da verificare.
     * @throws ResponseStatusException con stato 400 se un ID è non valido.
     */
    protected void validateIds(String... ids) {
        for (String id : ids) {
            if (id == null || id.trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Errore Validazione: ID non validi.");
            }
        }
    }

    /**
     * Valida che un utente abbia il ruolo specificato.
     *
     * @param user         l'utente da controllare
     * @param expectedRole il ruolo atteso
     * @param errorMessage il messaggio di errore da lanciare se il ruolo non
     *                     corrisponde
     * @throws ResponseStatusException con stato 403 se il ruolo non corrisponde
     */
    protected void validateUserRole(User user, hackhub.app.Core.Enums.Ruolo expectedRole, String errorMessage) {
        if (user.getRuolo() != expectedRole) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, errorMessage);
        }
    }
}
