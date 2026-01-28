package hackhub.app.Application.Strategies;

import org.springframework.stereotype.Component;

@Component
public class WebexLinkStrategy implements LinkStrategy {
    @Override
    public boolean isValid(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }
        return url.toLowerCase().contains("webex.com");
    }

    @Override
    public String getPlatformName() {
        return "Webex";
    }
}
