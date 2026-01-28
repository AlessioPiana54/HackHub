package hackhub.app.Application.Strategies;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class LinkStrategyContext {

    // Spring inietterà qui a Runtime tutte le classi che implementano l'interfaccia
    // LinkStrategy
    // che trova nel contesto (es. GitHubLinkStrategy, GoogleMeetLinkStrategy, ecc.)
    private final List<LinkStrategy> strategies;

    @Autowired
    public LinkStrategyContext(List<LinkStrategy> strategies) {
        this.strategies = strategies;
    }

    public boolean validate(String url, List<String> allowedPlatforms) {
        if (url == null || url.isBlank()) {
            return false;
        }

        // Scorro tutte le strategie disponibili (GitHub, Meet, Webex...)
        for (LinkStrategy strategy : strategies) {
            // Controllo se la strategia corrente è tra quelle richieste (es. solo "GitHub")
            if (allowedPlatforms.contains(strategy.getPlatformName())) {
                // Se la piattaforma è consentita, provo a validare l'URL con quella strategia
                if (strategy.isValid(url)) {
                    return true;
                }
            }
        }

        // Se nessuna delle strategie consentite ha validato l'URL, allora non è valido
        return false;
    }
}
