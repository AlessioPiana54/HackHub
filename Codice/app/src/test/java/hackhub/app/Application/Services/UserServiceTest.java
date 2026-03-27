package hackhub.app.Application.Services;

import hackhub.app.Application.DTOs.UserDTO;
import hackhub.app.Application.Exceptions.EntityNotFoundException;
import hackhub.app.Application.IRepositories.IUserRepository;
import hackhub.app.Application.IUnitOfWork.IUnitOfWork;
import hackhub.app.Application.Requests.UpdateProfileRequest;
import hackhub.app.Core.Enums.Ruolo;
import hackhub.app.Core.POJO_Entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private IUnitOfWork unitOfWork;
    @Mock private IUserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        when(unitOfWork.userRepository()).thenReturn(userRepository);
        userService = new UserService(unitOfWork);
    }

    // --- getUserProfile ---

    @Test
    void getUserProfile_shouldThrow_whenUserNotFound() {
        when(userRepository.findById("u-999")).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
            EntityNotFoundException.class,
            () -> userService.getUserProfile("u-999")
        );
        assertEquals("Utente non trovato.", ex.getMessage());
    }

    @Test
    void getUserProfile_shouldReturnDTO_whenUserFound() {
        User user = new User("Mario", "Rossi", "mario@hackhub.it", "hash", Ruolo.UTENTE_SENZA_TEAM);
        user.setId("u-1");
        when(userRepository.findById("u-1")).thenReturn(Optional.of(user));

        UserDTO dto = userService.getUserProfile("u-1");

        assertEquals("u-1", dto.getId());
        assertEquals("Mario", dto.getNome());
        assertEquals("Rossi", dto.getCognome());
        assertEquals("mario@hackhub.it", dto.getEmail());
        assertEquals(Ruolo.UTENTE_SENZA_TEAM, dto.getRuolo());
    }

    // --- updateProfile ---

    @Test
    void updateProfile_shouldThrow_whenUserNotFound() {
        when(userRepository.findById("u-999")).thenReturn(Optional.empty());

        assertThrows(
            EntityNotFoundException.class,
            () -> userService.updateProfile("u-999", new UpdateProfileRequest("Luca", "Bianchi"))
        );
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateProfile_shouldUpdateBothFields_whenValid() {
        User user = new User("Mario", "Rossi", "mario@hackhub.it", "hash", Ruolo.UTENTE_SENZA_TEAM);
        user.setId("u-1");
        when(userRepository.findById("u-1")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserDTO result = userService.updateProfile("u-1", new UpdateProfileRequest("Luca", "Bianchi"));

        assertEquals("Luca", result.getNome());
        assertEquals("Bianchi", result.getCognome());
        verify(userRepository).save(user);
    }

    @Test
    void updateProfile_shouldIgnoreBlankNome() {
        User user = new User("Mario", "Rossi", "mario@hackhub.it", "hash", Ruolo.UTENTE_SENZA_TEAM);
        user.setId("u-1");
        when(userRepository.findById("u-1")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // blank nome → nome should remain unchanged
        UserDTO result = userService.updateProfile("u-1", new UpdateProfileRequest("  ", "Verdi"));

        assertEquals("Mario", result.getNome());
        assertEquals("Verdi", result.getCognome());
    }

    // --- getUsersByRuolo ---

    @Test
    void getUsersByRuolo_shouldReturnMappedList() {
        User u1 = new User("Mario", "Rossi", "mario@hackhub.it", "hash", Ruolo.ORGANIZZATORE);
        u1.setId("u-1");
        User u2 = new User("Luigi", "Verdi", "luigi@hackhub.it", "hash", Ruolo.ORGANIZZATORE);
        u2.setId("u-2");
        when(userRepository.findByRuolo(Ruolo.ORGANIZZATORE)).thenReturn(List.of(u1, u2));

        List<UserDTO> result = userService.getUsersByRuolo(Ruolo.ORGANIZZATORE);

        assertEquals(2, result.size());
        assertEquals("mario@hackhub.it", result.get(0).getEmail());
        assertEquals("luigi@hackhub.it", result.get(1).getEmail());
    }

    @Test
    void getUsersByRuolo_shouldReturnEmptyList_whenNoUsersFound() {
        when(userRepository.findByRuolo(Ruolo.GIUDICE)).thenReturn(List.of());

        List<UserDTO> result = userService.getUsersByRuolo(Ruolo.GIUDICE);

        assertTrue(result.isEmpty());
    }
}
