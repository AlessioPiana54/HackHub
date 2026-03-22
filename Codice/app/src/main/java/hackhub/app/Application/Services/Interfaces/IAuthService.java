package hackhub.app.Application.Services.Interfaces;

import hackhub.app.Application.Requests.LoginRequest;
import hackhub.app.Application.Requests.RegisterRequest;

public interface IAuthService {
    void register(RegisterRequest request);
    String login(LoginRequest request);
    void logout(String token);
}
