package hackhub.app.Application.Scheduler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import hackhub.app.Application.IRepositories.IHackathonRepository;
import hackhub.app.Core.Enums.StatoHackathon;
import hackhub.app.Core.POJO_Entities.Hackathon;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HackathonSchedulerTest {

    @Mock private IHackathonRepository hackathonRepository;

    private HackathonScheduler scheduler;

    @BeforeEach
    void setUp() {
        scheduler = new HackathonScheduler(hackathonRepository);
    }

    /**
     * Costruisce un hackathon con le date espresse in minuti rispetto ad adesso.
     */
    private Hackathon hackathon(StatoHackathon stato, int inizioIscr, int fineIscr, int inizio, int fine) {
        LocalDateTime now = LocalDateTime.now();
        Hackathon h = new Hackathon();
        h.setPeriodoIscrizione(now.plusMinutes(inizioIscr), now.plusMinutes(fineIscr));
        h.setDurata(now.plusMinutes(inizio), now.plusMinutes(fine));
        h.setStato(stato);
        return h;
    }

    private void run(Hackathon h) {
        when(hackathonRepository.findByStatoNotIn(anyCollection())).thenReturn(List.of(h));
        scheduler.updateHackathonStates();
    }

    @Test
    void inAttesa_becomesInIscrizione_whenRegistrationWindowIsOpen() {
        Hackathon h = hackathon(StatoHackathon.IN_ATTESA, -1, 10, 20, 30);
        run(h);
        assertEquals(StatoHackathon.IN_ISCRIZIONE, h.getStato());
        verify(hackathonRepository).save(h);
    }

    @Test
    void inAttesa_staysInAttesa_beforeRegistration() {
        Hackathon h = hackathon(StatoHackathon.IN_ATTESA, 5, 10, 20, 30);
        run(h);
        assertEquals(StatoHackathon.IN_ATTESA, h.getStato());
        verify(hackathonRepository, never()).save(any());
    }

    @Test
    void inIscrizione_returnsInAttesa_afterDeadlineBeforeStart() {
        Hackathon h = hackathon(StatoHackathon.IN_ISCRIZIONE, -20, -1, 10, 30);
        run(h);
        assertEquals(StatoHackathon.IN_ATTESA, h.getStato());
    }

    @Test
    void inIscrizione_becomesInCorso_whenEventStarted() {
        Hackathon h = hackathon(StatoHackathon.IN_ISCRIZIONE, -30, -10, -1, 30);
        run(h);
        assertEquals(StatoHackathon.IN_CORSO, h.getStato());
    }

    @Test
    void inAttesa_becomesInCorso_whenEventStarted() {
        Hackathon h = hackathon(StatoHackathon.IN_ATTESA, -30, -10, -1, 30);
        run(h);
        assertEquals(StatoHackathon.IN_CORSO, h.getStato());
    }

    @Test
    void inCorso_becomesInValutazione_afterEnd() {
        Hackathon h = hackathon(StatoHackathon.IN_CORSO, -60, -50, -40, -1);
        run(h);
        assertEquals(StatoHackathon.IN_VALUTAZIONE, h.getStato());
    }

    @Test
    void inCorso_staysInCorso_beforeEnd() {
        Hackathon h = hackathon(StatoHackathon.IN_CORSO, -60, -50, -40, 10);
        run(h);
        assertEquals(StatoHackathon.IN_CORSO, h.getStato());
        verify(hackathonRepository, never()).save(any());
    }
}
