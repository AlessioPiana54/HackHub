package hackhub.app.Application.DTOs;

/**
 * DTO per la risposta di autenticazione (login/register).
 */
public class AuthResponse {
    private String token;
    private UserDTO user;

    public AuthResponse(String token, UserDTO user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public UserDTO getUser() {
        return user;
    }
}
