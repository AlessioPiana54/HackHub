package hackhub.app.Application.Services;

import hackhub.app.Application.IRepositories.IHackathonRepository;
import hackhub.app.Application.IRepositories.ISottomissioneRepository;
import hackhub.app.Application.IRepositories.IValutazioneRepository;
import hackhub.app.Application.IUnitOfWork.IUnitOfWork;
import hackhub.app.Core.Enums.StatoHackathon;
import hackhub.app.Core.POJO_Entities.Hackathon;
import hackhub.app.Core.POJO_Entities.Sottomissione;
import hackhub.app.Core.POJO_Entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HackathonServiceTest {

    @Mock
    private IUnitOfWork unitOfWork;
    @Mock
    private IHackathonRepository hackathonRepository;
    @Mock
    private ISottomissioneRepository sottomissioneRepository;
    @Mock
    private IValutazioneRepository valutazioneRepository;

    @InjectMocks
    private HackathonService hackathonService;

    private Hackathon hackathon;
    private User giudice;
    private final String HACKATHON_ID = "hack1";
    private final String GIUDICE_ID = "judge1";

    @BeforeEach
    void setUp() throws Exception {
        // Mock Repositories returning from UnitOfWork
        lenient().when(unitOfWork.hackathonRepository()).thenReturn(hackathonRepository);
        lenient().when(unitOfWork.sottomissioneRepository()).thenReturn(sottomissioneRepository);
        lenient().when(unitOfWork.valutazioneRepository()).thenReturn(valutazioneRepository);

        // Setup User (Judge)
        giudice = new User();
        setId(giudice, GIUDICE_ID);

        // Setup Hackathon
        hackathon = new Hackathon();
        hackathon.setStato(StatoHackathon.IN_VALUTAZIONE);
        // We need to set the Judge in the Hackathon. Hackathon has no setGiudice (it's
        // in constructor or via reflection if fields private)
        // Check Hackathon.java -> it has Private fields. It has a constructor.
        // It has NO setGiudice method? Let's assume I can use Reflection.
        setField(hackathon, "giudice", giudice);
        setField(hackathon, "id", HACKATHON_ID); // set ID just in case
    }

    @Test
    void terminaFaseValutazione_Success() {
        // Arrange
        when(hackathonRepository.findById(HACKATHON_ID)).thenReturn(Optional.of(hackathon));

        Sottomissione sub = new Sottomissione();
        setId(sub, "sub1");
        when(sottomissioneRepository.findByPartecipazioneHackathonId(HACKATHON_ID)).thenReturn(List.of(sub));
        when(valutazioneRepository.existsBySottomissioneId("sub1")).thenReturn(true);

        // Act
        hackathonService.terminaFaseValutazione(HACKATHON_ID, GIUDICE_ID);

        // Assert
        assertEquals(StatoHackathon.IN_PREMIAZIONE, hackathon.getStato());
        verify(hackathonRepository).save(hackathon);
    }

    @Test
    void terminaFaseValutazione_Fail_NotEvaluated() {
        // Arrange
        when(hackathonRepository.findById(HACKATHON_ID)).thenReturn(Optional.of(hackathon));

        Sottomissione sub = new Sottomissione();
        setId(sub, "sub1");
        when(sottomissioneRepository.findByPartecipazioneHackathonId(HACKATHON_ID)).thenReturn(List.of(sub));
        when(valutazioneRepository.existsBySottomissioneId("sub1")).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            hackathonService.terminaFaseValutazione(HACKATHON_ID, GIUDICE_ID);
        });
        assertEquals(StatoHackathon.IN_VALUTAZIONE, hackathon.getStato()); // Should not change
    }

    @Test
    void terminaFaseValutazione_Fail_WrongJudge() {
        // Arrange
        when(hackathonRepository.findById(HACKATHON_ID)).thenReturn(Optional.of(hackathon));

        // Act & Assert
        assertThrows(SecurityException.class, () -> {
            hackathonService.terminaFaseValutazione(HACKATHON_ID, "wrong_judge");
        });
    }

    @Test
    void terminaFaseValutazione_Fail_WrongState() {
        // Arrange
        hackathon.setStato(StatoHackathon.IN_CORSO); // Not IN_VALUTAZIONE
        when(hackathonRepository.findById(HACKATHON_ID)).thenReturn(Optional.of(hackathon));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            hackathonService.terminaFaseValutazione(HACKATHON_ID, GIUDICE_ID);
        });
    }

    // Helper for reflection
    private void setId(Object target, String id) {
        try {
            Field field = target.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(target, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
