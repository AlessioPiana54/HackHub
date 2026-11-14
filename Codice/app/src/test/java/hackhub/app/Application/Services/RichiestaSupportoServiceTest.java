package hackhub.app.Application.Services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import hackhub.app.Application.Exceptions.BusinessRuleException;
import hackhub.app.Application.Exceptions.UnauthorizedOperationException;
import hackhub.app.Application.IRepositories.IRichiestaSupportoRepository;
import hackhub.app.Application.IUnitOfWork.IUnitOfWork;
import hackhub.app.Application.Requests.CreaRichiestaSupportoRequest;
import hackhub.app.Application.Requests.ProponiCallRequest;
import hackhub.app.Application.Strategies.LinkStrategyContext;
import hackhub.app.Core.Enums.Ruolo;
import hackhub.app.Core.Enums.StatoHackathon;
import hackhub.app.Core.POJO_Entities.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RichiestaSupportoServiceTest {

    @Mock private IUnitOfWork unitOfWork;
    @Mock private IRichiestaSupportoRepository richiestaRepository;
    @Mock private EntityFinder entityFinder;
    @Mock private LinkStrategyContext linkStrategyContext;

    private RichiestaSupportoService service;

    private User leader;
    private User mentore;
    private Team team;
    private Hackathon hackathon;
    private Partecipazione partecipazione;

    @BeforeEach
    void setUp() {
        // AuthorizationChecker reale: verifichiamo i permessi effettivi
        service = new RichiestaSupportoService(unitOfWork, entityFinder, new AuthorizationChecker(), linkStrategyContext);

        leader = new User("Mario", "Rossi", "m@h.it", "h", Ruolo.LEADER_TEAM);
        leader.setId("leader-1");
        mentore = new User("Paolo", "Gialli", "p@h.it", "h", Ruolo.MENTORE);
        mentore.setId("mentore-1");
        team = new Team("Alpha", leader);
        hackathon = new Hackathon();
        hackathon.setMentori(List.of(mentore));
        partecipazione = new Partecipazione(team, hackathon);
    }

    @Test
    void creaRichiesta_shouldThrow_whenHackathonNotInCorso() {
        hackathon.setStato(StatoHackathon.IN_ISCRIZIONE);
        when(entityFinder.findTeamOrThrow("team-1")).thenReturn(team);
        when(entityFinder.findHackathonOrThrow("hack-1")).thenReturn(hackathon);

        assertThrows(BusinessRuleException.class, () -> service.creaRichiestaSupporto(
            new CreaRichiestaSupportoRequest("hack-1", "team-1", "Aiuto"), "leader-1"));
    }

    @Test
    void creaRichiesta_shouldThrow_whenUserNotInTeam() {
        hackathon.setStato(StatoHackathon.IN_CORSO);
        when(entityFinder.findTeamOrThrow("team-1")).thenReturn(team);
        when(entityFinder.findHackathonOrThrow("hack-1")).thenReturn(hackathon);

        assertThrows(UnauthorizedOperationException.class, () -> service.creaRichiestaSupporto(
            new CreaRichiestaSupportoRequest("hack-1", "team-1", "Aiuto"), "estraneo"));
    }

    @Test
    void creaRichiesta_shouldSave_whenValid() {
        hackathon.setStato(StatoHackathon.IN_CORSO);
        when(entityFinder.findTeamOrThrow("team-1")).thenReturn(team);
        when(entityFinder.findHackathonOrThrow("hack-1")).thenReturn(hackathon);
        when(entityFinder.findPartecipazioneOrThrow("team-1", "hack-1")).thenReturn(partecipazione);
        when(entityFinder.findUserOrThrow("leader-1")).thenReturn(leader);
        when(unitOfWork.richiestaSupportoRepository()).thenReturn(richiestaRepository);

        service.creaRichiestaSupporto(new CreaRichiestaSupportoRequest("hack-1", "team-1", "Aiuto"), "leader-1");

        verify(richiestaRepository).save(any(RichiestaSupporto.class));
    }

    @Test
    void proponiCall_shouldThrow_whenLinkNotMeetOrWebex() {
        when(linkStrategyContext.validate(eq("https://zoom.us/j/1"), anyList())).thenReturn(false);

        assertThrows(BusinessRuleException.class, () -> service.proponiCall(
            new ProponiCallRequest(null, "https://zoom.us/j/1", LocalDateTime.now().plusDays(1)), "mentore-1", "req-1"));
    }

    @Test
    void proponiCall_shouldThrow_whenNotMentorOfHackathon() {
        RichiestaSupporto richiesta = new RichiestaSupporto(partecipazione, leader, "Aiuto");
        when(linkStrategyContext.validate(anyString(), anyList())).thenReturn(true);
        when(unitOfWork.richiestaSupportoRepository()).thenReturn(richiestaRepository);
        when(richiestaRepository.findById("req-1")).thenReturn(Optional.of(richiesta));

        assertThrows(UnauthorizedOperationException.class, () -> service.proponiCall(
            new ProponiCallRequest(null, "https://meet.google.com/abc", LocalDateTime.now().plusDays(1)), "altro-mentore", "req-1"));
    }

    @Test
    void proponiCall_shouldSetCall_whenMentorOfHackathon() {
        RichiestaSupporto richiesta = new RichiestaSupporto(partecipazione, leader, "Aiuto");
        LocalDateTime data = LocalDateTime.now().plusDays(1);
        when(linkStrategyContext.validate(anyString(), anyList())).thenReturn(true);
        when(unitOfWork.richiestaSupportoRepository()).thenReturn(richiestaRepository);
        when(richiestaRepository.findById("req-1")).thenReturn(Optional.of(richiesta));
        when(richiestaRepository.save(richiesta)).thenReturn(richiesta);

        var dto = service.proponiCall(
            new ProponiCallRequest(null, "https://meet.google.com/abc", data), "mentore-1", "req-1");

        assertEquals("https://meet.google.com/abc", dto.getLinkCall());
        assertEquals(data, dto.getDataCall());
    }

    @Test
    void getRichiestePerMentore_shouldThrow_whenNotMentor() {
        when(entityFinder.findHackathonOrThrow("hack-1")).thenReturn(hackathon);

        assertThrows(UnauthorizedOperationException.class, () -> service.getRichiestePerMentore("hack-1", "leader-1"));
    }
}
