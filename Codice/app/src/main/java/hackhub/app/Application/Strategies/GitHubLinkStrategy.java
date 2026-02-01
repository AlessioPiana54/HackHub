package hackhub.app.Application.Strategies;

import org.springframework.stereotype.Component;

/**
 * Strategia di validazione per i link di GitHub.
 * Verifica se l'URL appartiene al dominio github.com.
 */
@Component
public class GitHubLinkStrategy implements LinkStrategy {
    /**
     * Verifica la validità di un URL GitHub.
     *
     * @param url l'indirizzo URL da controllare
     * @return true se l'URL contiene "github.com", false altrimenti o se è
     *         nullo/vuoto
     */
    @Override
    public boolean isValid(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }
        return url.toLowerCase().contains("github.com");
    }

    /**
     * Restituisce il nome della piattaforma.
     *
     * @return "GitHub"
     */
    @Override
    public String getPlatformName() {
        return "GitHub";
    }
}
