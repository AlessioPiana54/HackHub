package hackhub.app.Infrastructure.Utils;

import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import hackhub.app.Application.Utils.IPasswordHasher;

@Component
public class PasswordHasher implements IPasswordHasher {

    @Override
    public String hash(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Errore durante l'hashing della password", e);
        }
    }

    @Override
    public boolean verify(String password, String storedHash) {
        return hash(password).equals(storedHash);
    }
}
