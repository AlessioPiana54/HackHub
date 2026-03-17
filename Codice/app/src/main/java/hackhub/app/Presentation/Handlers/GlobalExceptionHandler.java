package hackhub.app.Presentation.Handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Gestore globale delle eccezioni per l'applicazione.
 * Intercetta le eccezioni lanciate dai controller e restituisce risposte HTTP
 * appropriate.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Gestisce le eccezioni di tipo IllegalArgumentException e
     * IllegalStateException.
     * Restituisce uno stato HTTP 400 (Bad Request).
     *
     * @param e L'eccezione catturata.
     * @return Una ResponseEntity con lo stato 400 e il messaggio dell'errore.
     */
    @ExceptionHandler({ IllegalArgumentException.class, IllegalStateException.class })
    public ResponseEntity<hackhub.app.Application.DTOs.MessageResponse> handleBadRequest(RuntimeException e) {
        if ("Credenziali non valide.".equals(e.getMessage())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new hackhub.app.Application.DTOs.MessageResponse(e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new hackhub.app.Application.DTOs.MessageResponse(e.getMessage()));
    }

    /**
     * Gestisce le eccezioni di tipo SecurityException.
     * Restituisce uno stato HTTP 403 (Forbidden).
     *
     * @param e L'eccezione di sicurezza catturata.
     * @return Una ResponseEntity con lo stato 403 e il messaggio dell'errore.
     */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<hackhub.app.Application.DTOs.MessageResponse> handleForbidden(SecurityException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new hackhub.app.Application.DTOs.MessageResponse(e.getMessage()));
    }

    /**
     * Gestisce tutte le altre eccezioni non specifiche.
     * Restituisce uno stato HTTP 500 (Internal Server Error).
     *
     * @param e L'eccezione generica catturata.
     * @return Una ResponseEntity con lo stato 500 e un messaggio di errore
     *         generico.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<hackhub.app.Application.DTOs.MessageResponse> handleGenericException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new hackhub.app.Application.DTOs.MessageResponse("Errore interno del server: " + e.getMessage()));
    }
}
