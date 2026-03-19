package hackhub.app.Application.Services;

import hackhub.app.Application.IUnitOfWork.IUnitOfWork;
import hackhub.app.Application.Requests.LoginRequest;
import hackhub.app.Application.Requests.RegisterRequest;
import hackhub.app.Application.Utils.IJwtService;
import hackhub.app.Application.Utils.IPasswordHasher;
import hackhub.app.Core.POJO_Entities.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servizio responsabile della gestione dell'autenticazione e registrazione
 * degli utenti.
 */
@Service
@Transactional
public class AuthService extends AbstractService {

  private final IPasswordHasher passwordHasher;
  private final IJwtService jwtService;

  public AuthService(
    IUnitOfWork unitOfWork,
    EntityFinder entityFinder,
    AuthorizationChecker authorizationChecker,
    IPasswordHasher passwordHasher,
    IJwtService jwtService
  ) {
    super(unitOfWork, entityFinder, authorizationChecker);
    this.passwordHasher = passwordHasher;
    this.jwtService = jwtService;
  }

  /**
   * Registra un nuovo utente nel sistema.
   *
   * @param request i dati per la registrazione (nome, cognome, email, password)
   * @throws IllegalArgumentException se l'email è già presente nel sistema
   */
  public void register(RegisterRequest request) {
    if (unitOfWork.userRepository().findByEmail(request.getEmail()) != null) {
      throw new IllegalArgumentException("Email già registrata.");
    }

    // Hashing della password
    String hashedPassword = passwordHasher.hash(request.getPassword());

    unitOfWork
      .userRepository()
      .save(
        new User(
          request.getNome(),
          request.getCognome(),
          request.getEmail(),
          hashedPassword,
          hackhub.app.Core.Enums.Ruolo.UTENTE_SENZA_TEAM
        )
      );
  }

  /**
   * Effettua il login di un utente.
   *
   * @param request i dati per il login (email, password)
   * @return il token di sessione generato
   * @throws IllegalArgumentException se l'utente non viene trovato o la password
   *                                  è errata
   */
  public String login(LoginRequest request) {
    User user = unitOfWork.userRepository().findByEmail(request.getEmail());
    if (user == null) {
      throw new IllegalArgumentException("Credenziali non valide.");
    }

    if (!passwordHasher.verify(request.getPassword(), user.getPassword())) {
      throw new IllegalArgumentException("Credenziali non valide.");
    }

    return jwtService.generateToken(user);
  }

  /**
   * Effettua il logout invalidando il token di sessione.
   *
   * @param token il token di sessione da invalidare
   */
  public void logout(String token) {
    // JWT stateless: logout gestito lato client
  }
}
