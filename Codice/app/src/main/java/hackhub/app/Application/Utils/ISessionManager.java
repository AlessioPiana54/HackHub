package hackhub.app.Application.Utils;

import hackhub.app.Core.POJO_Entities.User;

/**
 * Interfaccia per la gestione delle sessioni utente.
 */
public interface ISessionManager {
    /**
     * Crea una nuova sessione per un utente autenticato.
     *
     * @param user L'utente per cui creare la sessione.
     * @return Il token di sessione generato.
     */
    String createSession(User user);

    /**
     * Recupera l'utente associato a un determinato token di sessione.
     *
     * @param token Il token di sessione.
     * @return L'utente associato al token, o null se il token non è valido o
     *         scaduto.
     */
    User getUser(String token);

    /**
     * Invalida una sessione esistente (logout).
     *
     * @param token Il token della sessione da invalidare.
     */
    void invalidateSession(String token);
}
