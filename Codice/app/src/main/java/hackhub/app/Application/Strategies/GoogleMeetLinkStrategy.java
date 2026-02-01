package hackhub.app.Application.Strategies;

import org.springframework.stereotype.Component;

/**
 * Strategia di validazione per i link di Google Meet.
 * Verifica se l'URL appartiene al dominio meet.google.com.
 */
@Component
public class GoogleMeetLinkStrategy implements LinkStrategy {
    /**
     * Verifica la validità di un URL Google Meet.
     *
     * @param url l'indirizzo URL da controllare
     * @return true se l'URL contiene "meet.google.com", false altrimenti o se è
     *         nullo/vuoto
     */
    @Override
    public boolean isValid(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }
        return url.toLowerCase().contains("meet.google.com");
    }

    /**
     * Restituisce il nome della piattaforma.
     *
     * @return "Google Meet"
     */
    @Override
    public String getPlatformName() {
        return "Google Meet";
    }
}
