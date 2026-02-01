package hackhub.app.Application.Strategies;

import org.springframework.stereotype.Component;
import java.util.List;

/**
 * Contesto che gestisce l'esecuzione delle strategie di validazione dei link.
 * Mantiene un riferimento a tutte le strategie disponibili e delega la
 * validazione a quella corretta in base alla piattaforma richiesta.
 */
@Component
public class LinkStrategyContext {

    // Spring inietterà qui a Runtime tutte le classi che implementano l'interfaccia
    // LinkStrategy
    // che trova nel contesto (es. GitHubLinkStrategy, GoogleMeetLinkStrategy, ecc.)
    private final List<LinkStrategy> strategies;

    public LinkStrategyContext(List<LinkStrategy> strategies) {
        this.strategies = strategies;
    }

    /**
     * Valida un URL verificando che appartenga a una delle piattaforme consentite
     * e che rispetti le regole della strategia corrispondente.
     *
     * @param url              l'URL da validare
     * @param allowedPlatforms la lista dei nomi delle piattaforme accettate
     * @return true se l'URL è valido per una delle piattaforme consentite, false
     *         altrimenti
     */
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
