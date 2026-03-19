package hackhub.app.Application.Services;

import hackhub.app.Application.IUnitOfWork.IUnitOfWork;
import hackhub.app.Application.Requests.CreaHackathonRequest;
import hackhub.app.Application.Utils.IPaymentManager;
import hackhub.app.Core.Enums.Ruolo;
import hackhub.app.Core.Enums.StatoHackathon;
import hackhub.app.Core.POJO_Entities.Hackathon;
import hackhub.app.Core.POJO_Entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HackathonServiceTest {

    @Mock private IUnitOfWork unitOfWork;
    @Mock private EntityFinder entityFinder;
    @Mock private AuthorizationChecker authorizationChecker;
    @Mock private IPaymentManager paymentService;

    private HackathonService hackathonService;

    @BeforeEach
    void setUp() {
        hackathonService = new HackathonService(unitOfWork, entityFinder, authorizationChecker, paymentService);
    }

    @Test
    void creaHackathon_shouldThrow_whenUserIsNotOrganizzatore() {
        String organizzatoreId = "user-org";
        String giudiceId = "user-judge";

        User organizzatore = new User();
        organizzatore.setId(organizzatoreId);
        organizzatore.setRuolo(Ruolo.GIUDICE);

        User giudice = new User();
        giudice.setId(giudiceId);
        giudice.setRuolo(Ruolo.GIUDICE);

        CreaHackathonRequest request = new CreaHackathonRequest(
                "Hack",
                "Reg",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(3),
                "Roma",
                null,
                100.0,
                giudiceId,
                List.of()
        );

        when(entityFinder.findUserOrThrow(organizzatoreId)).thenReturn(organizzatore);
        when(entityFinder.findUserOrThrow(giudiceId)).thenReturn(giudice);
        doThrow(new SecurityException("L'utente specificato come organizzatore non ha i permessi necessari."))
                .when(authorizationChecker)
                .validateUserRole(any(User.class), eq(Ruolo.ORGANIZZATORE), anyString());

        assertThrows(SecurityException.class, () -> hackathonService.creaHackathon(request, organizzatoreId));
    }

    @Test
    void terminaFaseValutazione_shouldThrow_whenNotInValutazione() {
        Hackathon hackathon = new Hackathon();
        hackathon.setStato(StatoHackathon.IN_CORSO);

        when(entityFinder.findHackathonOrThrow("hack-1")).thenReturn(hackathon);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> hackathonService.terminaFaseValutazione("hack-1", "judge-1")
        );
        assertEquals("L'Hackathon non è in fase di valutazione", ex.getMessage());
    }

    @Test
    void proclamaVincitore_shouldThrow_whenNotOrganizzatore() {
        User organizzatore = new User();
        organizzatore.setId("org-1");
        organizzatore.setRuolo(Ruolo.ORGANIZZATORE);

        Hackathon hackathon = new Hackathon();
        hackathon.setOrganizzatore(organizzatore);
        hackathon.setStato(StatoHackathon.IN_PREMIAZIONE);

        when(entityFinder.findHackathonOrThrow("hack-1")).thenReturn(hackathon);

        SecurityException ex = assertThrows(
                SecurityException.class,
                () -> hackathonService.proclamaVincitore("hack-1", "team-1", "not-org")
        );
        assertEquals("Solo l'organizzatore può proclamare il vincitore.", ex.getMessage());
    }
}

