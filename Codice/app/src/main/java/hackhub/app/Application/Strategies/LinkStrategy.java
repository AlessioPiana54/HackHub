package hackhub.app.Application.Strategies;

/**
 * Interfaccia che definisce la strategia per la validazione dei link.
 * Ogni implementazione gestisce una logica specifica per una particolare
 * piattaforma.
 */
public interface LinkStrategy {
    /**
     * Verifica se l'URL fornito è valido per questa specifica strategia.
     *
     * @param url l'indirizzo URL da validare
     * @return true se l'URL è valido, false altrimenti
     */
    boolean isValid(String url);

    /**
     * Restituisce il nome identificativo della piattaforma gestita dalla strategia.
     *
     * @return il nome della piattaforma (es. "GitHub", "Google Meet")
     */
    String getPlatformName();
}
