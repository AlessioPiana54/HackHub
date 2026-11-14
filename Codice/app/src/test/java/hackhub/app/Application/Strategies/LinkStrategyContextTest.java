package hackhub.app.Application.Strategies;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class LinkStrategyContextTest {

    private final LinkStrategyContext context = new LinkStrategyContext(List.of(
        new GitHubLinkStrategy(),
        new GoogleMeetLinkStrategy(),
        new WebexLinkStrategy()
    ));

    private static final List<String> REPO = List.of("GitHub");
    private static final List<String> CALL = List.of("Google Meet", "Webex");

    @ParameterizedTest
    @ValueSource(strings = {
        "https://github.com/team/progetto",
        "http://github.com/team/progetto",
        "https://www.github.com/team/progetto",
        "HTTPS://GitHub.com/Team/Progetto"
    })
    void validGitHubLinks(String url) {
        assertTrue(context.validate(url, REPO));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "https://evil.example/?github.com",
        "https://github.com.evil.example/repo",
        "https://notgithub.com/repo",
        "ftp://github.com/repo",
        "github.com/team/progetto",
        "javascript:alert('github.com')",
        "   "
    })
    void invalidGitHubLinks(String url) {
        assertFalse(context.validate(url, REPO));
    }

    @Test
    void nullLinkIsInvalid() {
        assertFalse(context.validate(null, REPO));
    }

    @Test
    void callLinksAcceptMeetAndWebexOnly() {
        assertTrue(context.validate("https://meet.google.com/abc-defg-hij", CALL));
        assertTrue(context.validate("https://unicam.webex.com/meet/mario", CALL));
        assertFalse(context.validate("https://zoom.us/j/123", CALL));
        assertFalse(context.validate("https://google.com/meet.google.com", CALL));
    }

    @Test
    void platformNotAllowedIsRejected() {
        // Un link GitHub valido non è accettato se è richiesta una piattaforma di call
        assertFalse(context.validate("https://github.com/team/progetto", CALL));
    }
}
