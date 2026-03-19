package hackhub.app.Application.Services;

import hackhub.app.Application.IRepositories.IUserRepository;
import hackhub.app.Application.IUnitOfWork.IUnitOfWork;
import hackhub.app.Application.Requests.LoginRequest;
import hackhub.app.Application.Requests.RegisterRequest;
import hackhub.app.Application.Utils.IJwtService;
import hackhub.app.Application.Utils.IPasswordHasher;
import hackhub.app.Core.Enums.Ruolo;
import hackhub.app.Core.POJO_Entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private IUnitOfWork unitOfWork;
    @Mock private IUserRepository userRepository;
    @Mock private EntityFinder entityFinder;
    @Mock private AuthorizationChecker authorizationChecker;
    @Mock private IPasswordHasher passwordHasher;
    @Mock private IJwtService jwtService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        when(unitOfWork.userRepository()).thenReturn(userRepository);
        authService = new AuthService(unitOfWork, entityFinder, authorizationChecker, passwordHasher, jwtService);
    }

    @Test
    void register_shouldThrow_whenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest("Mario", "Rossi", "mario@hackhub.it", "Test1234!");
        when(userRepository.findByEmail(request.getEmail())).thenReturn(new User());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> authService.register(request));
        assertEquals("Email già registrata.", ex.getMessage());
        verify(userRepository, never()).save(any());
        verify(passwordHasher, never()).hash(anyString());
    }

    @Test
    void register_shouldHashPassword_beforeSaving() {
        RegisterRequest request = new RegisterRequest("Mario", "Rossi", "mario@hackhub.it", "plain-pass");
        when(userRepository.findByEmail(request.getEmail())).thenReturn(null);
        when(passwordHasher.hash("plain-pass")).thenReturn("hashed-pass");

        authService.register(request);

        verify(passwordHasher).hash("plain-pass");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User saved = userCaptor.getValue();
        assertEquals("mario@hackhub.it", saved.getEmail());
        assertEquals("hashed-pass", saved.getPassword());
        assertNotEquals("plain-pass", saved.getPassword());
        assertEquals(Ruolo.UTENTE_SENZA_TEAM, saved.getRuolo());
    }

    @Test
    void login_shouldThrow_whenUserNotFound() {
        LoginRequest request = new LoginRequest("missing@hackhub.it", "Test1234!");
        when(userRepository.findByEmail(request.getEmail())).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> authService.login(request));
        assertEquals("Credenziali non valide.", ex.getMessage());
        verify(passwordHasher, never()).verify(anyString(), anyString());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void login_shouldReturnToken_whenCredentialsAreCorrect() {
        LoginRequest request = new LoginRequest("mario@hackhub.it", "Test1234!");
        User user = new User("Mario", "Rossi", "mario@hackhub.it", "stored-hash", Ruolo.UTENTE_SENZA_TEAM);
        user.setId("user-1");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(user);
        when(passwordHasher.verify("Test1234!", "stored-hash")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("fake-token");

        String token = authService.login(request);

        assertEquals("fake-token", token);
        verify(jwtService).generateToken(user);
    }
}

