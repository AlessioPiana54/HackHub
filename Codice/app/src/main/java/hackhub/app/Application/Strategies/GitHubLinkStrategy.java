package hackhub.app.Application.Strategies;

import org.springframework.stereotype.Component;

/**
 * Strategia di validazione per i link di GitHub.
 * Accetta solo URL http/https il cui host è github.com o un suo sottodominio.
 */
@Component
public class GitHubLinkStrategy implements LinkStrategy {

    private static final String DOMAIN = "github.com";

    @Override
    public boolean isValid(String url) {
        return LinkStrategy.hostMatches(url, DOMAIN);
    }

    @Override
    public String getPlatformName() {
        return "GitHub";
    }
}
