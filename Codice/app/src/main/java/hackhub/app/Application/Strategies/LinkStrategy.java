package hackhub.app.Application.Strategies;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

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

    /**
     * Verifica che l'URL sia http/https e che il suo host coincida con il dominio
     * indicato o con un suo sottodominio. Un URL che contiene il dominio solo nel
     * percorso o nella query (es. {@code https://evil.com/?github.com}) non è valido.
     *
     * @param url    l'URL da controllare
     * @param domain il dominio atteso (es. "github.com")
     * @return true se l'host appartiene al dominio
     */
    static boolean hostMatches(String url, String domain) {
        if (url == null || url.isBlank()) {
            return false;
        }
        try {
            URI uri = new URI(url.trim());
            String scheme = uri.getScheme();
            String host = uri.getHost();
            if (scheme == null || host == null) {
                return false;
            }
            scheme = scheme.toLowerCase(Locale.ROOT);
            if (!scheme.equals("http") && !scheme.equals("https")) {
                return false;
            }
            host = host.toLowerCase(Locale.ROOT);
            return host.equals(domain) || host.endsWith("." + domain);
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
