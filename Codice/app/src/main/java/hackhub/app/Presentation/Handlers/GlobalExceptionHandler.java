package hackhub.app.Presentation.Handlers;

import hackhub.app.Application.DTOs.MessageResponse;
import hackhub.app.Application.Exceptions.BusinessRuleException;
import hackhub.app.Application.Exceptions.DomainException;
import hackhub.app.Application.Exceptions.EntityNotFoundException;
import hackhub.app.Application.Exceptions.UnauthorizedOperationException;
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
   * Gestisce le eccezioni di tipo EntityNotFoundException.
   * Restituisce uno stato HTTP 404 (Not Found).
   *
   * @param e L'eccezione catturata.
   * @return Una ResponseEntity con lo stato 404 e il messaggio dell'errore.
   */
  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<MessageResponse> handleEntityNotFound(EntityNotFoundException e) {
    return ResponseEntity
      .status(HttpStatus.NOT_FOUND)
      .body(new MessageResponse(e.getMessage()));
  }

  @ExceptionHandler(UnauthorizedOperationException.class)
  public ResponseEntity<MessageResponse> handleUnauthorizedOperation(UnauthorizedOperationException e) {
    return ResponseEntity
      .status(HttpStatus.FORBIDDEN)
      .body(new MessageResponse(e.getMessage()));
  }

  @ExceptionHandler({BusinessRuleException.class, DomainException.class})
  public ResponseEntity<MessageResponse> handleDomainExceptions(RuntimeException e) {
    return ResponseEntity
      .status(HttpStatus.BAD_REQUEST)
      .body(new MessageResponse(e.getMessage()));
  }

  /**
   * Gestisce le eccezioni di tipo SecurityException.
   * Restituisce uno stato HTTP 403 (Forbidden).
   *
   * @param e L'eccezione di sicurezza catturata.
   * @return Una ResponseEntity con lo stato 403 e il messaggio dell'errore.
   */
  @ExceptionHandler(SecurityException.class)
  public ResponseEntity<MessageResponse> handleForbidden(SecurityException e) {
    return ResponseEntity
      .status(HttpStatus.FORBIDDEN)
      .body(new MessageResponse(e.getMessage()));
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
  public ResponseEntity<MessageResponse> handleGenericException(Exception e) {
    return ResponseEntity
      .status(HttpStatus.INTERNAL_SERVER_ERROR)
      .body(
        new MessageResponse("Errore interno del server: " + e.getMessage())
      );
  }
}
