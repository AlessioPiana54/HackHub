package hackhub.app.Infrastructure.Utils;

import hackhub.app.Application.Utils.ISessionManager;
import hackhub.app.Core.POJO_Entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Utility class for security-related operations.
 * Provides methods to extract current user information from session tokens.
 */
@Component
public class SecurityUtils {

  private static ISessionManager sessionManager;

  @Autowired
  public void setSessionManager(ISessionManager sessionManager) {
    SecurityUtils.sessionManager = sessionManager;
  }

  /**
   * Extracts the current user ID from the session token.
   * Note: This method requires the token to be passed explicitly since the project
   * uses custom session management rather than Spring Security.
   *
   * @param token the authorization token from the request header
   * @return the ID of the currently authenticated user
   * @throws IllegalStateException if no user is found for the token
   */
  public static String getCurrentUserId(String token) {
    if (sessionManager == null) {
      throw new IllegalStateException(
        "SessionManager non è stato inizializzato."
      );
    }

    User user = sessionManager.getUser(token);
    if (user == null) {
      throw new IllegalStateException(
        "Nessun utente autenticato trovato per il token fornito."
      );
    }

    return user.getId();
  }

  /**
   * Extracts the current user from the session token.
   *
   * @param token the authorization token from the request header
   * @return the currently authenticated user
   * @throws IllegalStateException if no user is found for the token
   */
  public static User getCurrentUser(String token) {
    if (sessionManager == null) {
      throw new IllegalStateException(
        "SessionManager non è stato inizializzato."
      );
    }

    User user = sessionManager.getUser(token);
    if (user == null) {
      throw new IllegalStateException(
        "Nessun utente autenticato trovato per il token fornito."
      );
    }

    return user;
  }

  /**
   * Checks if the provided token corresponds to an authenticated user.
   *
   * @param token the authorization token from the request header
   * @return true if a user is authenticated, false otherwise
   */
  public static boolean isCurrentUserAuthenticated(String token) {
    if (sessionManager == null) {
      return false;
    }

    return sessionManager.getUser(token) != null;
  }
}
