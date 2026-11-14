package hackhub.app.Application.Strategies;

import org.springframework.stereotype.Component;

/**
 * Strategia di validazione per i link di Google Meet.
 * Accetta solo URL http/https il cui host è meet.google.com o un suo sottodominio.
 */
@Component
public class GoogleMeetLinkStrategy implements LinkStrategy {

    private static final String DOMAIN = "meet.google.com";

    @Override
    public boolean isValid(String url) {
        return LinkStrategy.hostMatches(url, DOMAIN);
    }

    @Override
    public String getPlatformName() {
        return "Google Meet";
    }
}
