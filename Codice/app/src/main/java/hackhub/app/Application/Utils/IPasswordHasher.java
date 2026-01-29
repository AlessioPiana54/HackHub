package hackhub.app.Application.Utils;

public interface IPasswordHasher {
    String hash(String password);

    boolean verify(String password, String storedHash);
}
