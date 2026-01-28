package hackhub.app.Application.Strategies;

public interface LinkStrategy {
    boolean isValid(String url);

    String getPlatformName();
}
