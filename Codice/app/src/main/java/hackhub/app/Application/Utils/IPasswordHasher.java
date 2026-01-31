package hackhub.app.Application.Utils;

/**
 * Interfaccia per la gestione dell'hashing delle password.
 */
public interface IPasswordHasher {
    /**
     * Esegue l'hashing di una password in chiaro.
     *
     * @param password La password in chiaro da hashare.
     * @return L'hash della password.
     */
    String hash(String password);

    /**
     * Verifica se una password in chiaro corrisponde a un hash memorizzato.
     *
     * @param password   La password in chiaro da verificare.
     * @param storedHash L'hash memorizzato con cui confrontare la password.
     * @return true se la password corrisponde all'hash, false altrimenti.
     */
    boolean verify(String password, String storedHash);
}
