package hackhub.app.Application.Services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import hackhub.app.Application.IUnitOfWork.IUnitOfWork;
import hackhub.app.Application.Requests.LoginRequest;
import hackhub.app.Application.Requests.RegisterRequest;
import hackhub.app.Application.Utils.ISessionManager;
import hackhub.app.Application.Utils.IPasswordHasher;
import hackhub.app.Core.POJO_Entities.User;

@Service
@Transactional
public class AuthService {

    private final IUnitOfWork unitOfWork;
    private final IPasswordHasher passwordHasher;
    private final ISessionManager sessionManager;

    @Autowired
    public AuthService(IUnitOfWork unitOfWork, IPasswordHasher passwordHasher, ISessionManager sessionManager) {
        this.unitOfWork = unitOfWork;
        this.passwordHasher = passwordHasher;
        this.sessionManager = sessionManager;
    }

    public void register(RegisterRequest request) {
        if (unitOfWork.userRepository().findByEmail(request.getEmail()) != null) {
            throw new IllegalArgumentException("Email già registrata.");
        }

        // Hashing della password
        String hashedPassword = passwordHasher.hash(request.getPassword());

        // Ruolo default UTENTE_SENZA_TEAM
        unitOfWork.userRepository()
                .save(new User(request.getNome(), request.getCognome(), request.getEmail(), hashedPassword,
                        hackhub.app.Core.Enums.Ruolo.UTENTE_SENZA_TEAM));
    }

    public String login(LoginRequest request) {
        User user = unitOfWork.userRepository().findByEmail(request.getEmail());
        if (user == null) {
            throw new IllegalArgumentException("Utente non trovato.");
        }

        if (!passwordHasher.verify(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Password non valida.");
        }

        return sessionManager.createSession(user);
    }

    public void logout(String token) {
        sessionManager.invalidateSession(token);
    }
}
