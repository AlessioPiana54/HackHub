package hackhub.app.Application.Utils;

import hackhub.app.Core.POJO_Entities.User;

public interface IJwtService {
    String generateToken(User user);
    String extractUserId(String token);
    String extractRuolo(String token);
    boolean isTokenValid(String token);
}

