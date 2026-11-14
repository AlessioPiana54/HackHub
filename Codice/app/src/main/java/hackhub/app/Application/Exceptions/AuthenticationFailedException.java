package hackhub.app.Application.Exceptions;

/**
 * Credenziali non valide in fase di login.
 * Mappata su HTTP 401 senza distinguere tra email inesistente e password errata.
 */
public class AuthenticationFailedException extends DomainException {
    public AuthenticationFailedException(String message) {
        super(message);
    }
}
