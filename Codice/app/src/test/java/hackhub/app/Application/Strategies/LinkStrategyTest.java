package hackhub.app.Application.Strategies;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.List;

public class LinkStrategyTest {

    @Test
    public void testGitHubStrategy() {
        LinkStrategy strategy = new GitHubLinkStrategy();
        Assertions.assertTrue(strategy.isValid("https://github.com/user/repo"));
        Assertions.assertFalse(strategy.isValid("https://gitlab.com/user/repo"));
        Assertions.assertFalse(strategy.isValid(null));
        Assertions.assertEquals("GitHub", strategy.getPlatformName());
    }

    @Test
    public void testGoogleMeetStrategy() {
        LinkStrategy strategy = new GoogleMeetLinkStrategy();
        Assertions.assertTrue(strategy.isValid("https://meet.google.com/abc-defg-hij"));
        Assertions.assertFalse(strategy.isValid("https://zoom.us/j/123456789"));
        Assertions.assertFalse(strategy.isValid(null));
        Assertions.assertEquals("Google Meet", strategy.getPlatformName());
    }

    @Test
    public void testWebexStrategy() {
        LinkStrategy strategy = new WebexLinkStrategy();
        Assertions.assertTrue(strategy.isValid("https://company.webex.com/meet/user"));
        Assertions.assertFalse(strategy.isValid("https://zoom.us/j/123456789"));
        Assertions.assertFalse(strategy.isValid(null));
        Assertions.assertEquals("Webex", strategy.getPlatformName());
    }

    @Test
    public void testLinkValidator() {
        LinkStrategyContext validator = new LinkStrategyContext(List.of(
                new GitHubLinkStrategy(),
                new GoogleMeetLinkStrategy(),
                new WebexLinkStrategy()));

        // Test GitHub validation
        Assertions.assertTrue(validator.validate("https://github.com/my/project", List.of("GitHub")));
        Assertions.assertFalse(validator.validate("https://gitlab.com/my/project", List.of("GitHub")));

        // Test Multi-platform validation
        Assertions.assertTrue(validator.validate("https://meet.google.com/abc", List.of("Google Meet", "Webex")));
        Assertions.assertTrue(validator.validate("https://company.webex.com/meet", List.of("Google Meet", "Webex")));
        Assertions.assertFalse(validator.validate("https://zoom.us/123", List.of("Google Meet", "Webex")));
    }
}
