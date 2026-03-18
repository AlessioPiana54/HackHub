package hackhub.app.Infrastructure.Utils;

import hackhub.app.Application.Utils.IPasswordHasher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Implementazione sicura del PasswordHasher utilizzando BCrypt.
 */
@Component
public class PasswordHasher implements IPasswordHasher {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String hash(String password) {
        return encoder.encode(password);
    }

    @Override
    public boolean verify(String password, String storedHash) {
        return encoder.matches(password, storedHash);
    }
}
