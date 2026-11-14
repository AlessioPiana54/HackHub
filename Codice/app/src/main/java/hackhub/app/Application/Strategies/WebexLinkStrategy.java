package hackhub.app.Application.Strategies;

import org.springframework.stereotype.Component;

/**
 * Strategia di validazione per i link di Webex.
 * Accetta solo URL http/https il cui host è webex.com o un suo sottodominio.
 */
@Component
public class WebexLinkStrategy implements LinkStrategy {

    private static final String DOMAIN = "webex.com";

    @Override
    public boolean isValid(String url) {
        return LinkStrategy.hostMatches(url, DOMAIN);
    }

    @Override
    public String getPlatformName() {
        return "Webex";
    }
}
