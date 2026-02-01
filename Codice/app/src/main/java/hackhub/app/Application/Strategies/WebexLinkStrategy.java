package hackhub.app.Application.Strategies;

import org.springframework.stereotype.Component;

/**
 * Strategia di validazione per i link di Webex.
 * Verifica se l'URL appartiene al dominio webex.com.
 */
@Component
public class WebexLinkStrategy implements LinkStrategy {
    /**
     * Verifica la validità di un URL Webex.
     *
     * @param url l'indirizzo URL da controllare
     * @return true se l'URL contiene "webex.com", false altrimenti o se è
     *         nullo/vuoto
     */
    @Override
    public boolean isValid(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }
        return url.toLowerCase().contains("webex.com");
    }

    /**
     * Restituisce il nome della piattaforma.
     *
     * @return "Webex"
     */
    @Override
    public String getPlatformName() {
        return "Webex";
    }
}
