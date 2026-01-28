package hackhub.app.Application.Strategies;

import org.springframework.stereotype.Component;

@Component
public class GoogleMeetLinkStrategy implements LinkStrategy {
    @Override
    public boolean isValid(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }
        return url.toLowerCase().contains("meet.google.com");
    }

    @Override
    public String getPlatformName() {
        return "Google Meet";
    }
}
