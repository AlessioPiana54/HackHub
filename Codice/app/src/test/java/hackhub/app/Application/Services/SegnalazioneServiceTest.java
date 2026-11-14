package hackhub.app.Application.Services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import hackhub.app.Application.Exceptions.UnauthorizedOperationException;
import hackhub.app.Application.IRepositories.ISegnalazioneRepository;
import hackhub.app.Application.IRepositories.ITeamRepository;
import hackhub.app.Application.IUnitOfWork.IUnitOfWork;
import hackhub.app.Application.Requests.CreaSegnalazioneRequest;
import hackhub.app.Core.Enums.Ruolo;
import hackhub.app.Core.POJO_Entities.*;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SegnalazioneServiceTest {

    @Mock private IUnitOfWork unitOfWork;
    @Mock private ISegnalazioneRepository segnalazioneRepository;
    @Mock private ITeamRepository teamRepository;
    @Mock private EntityFinder entityFinder;

    private SegnalazioneService service;

    private User organizzatore;
    private User mentore;
    private Hackathon hackathon;
    private Partecipazione partecipazione;

    @BeforeEach
    void setUp() {
        service = new SegnalazioneService(unitOfWork, entityFinder, new AuthorizationChecker());

        organizzatore = new User("Org", "Uno", "o@h.it", "h", Ruolo.ORGANIZZATORE);
        organizzatore.setId("org-1");
        mentore = new User("Paolo", "Gialli", "p@h.it", "h", Ruolo.MENTORE);
        mentore.setId("mentore-1");
        User leader = new User("Mario", "Rossi", "m@h.it", "h", Ruolo.LEADER_TEAM);
        leader.setId("leader-1");

        hackathon = new Hackathon();
        hackathon.setOrganizzatore(organizzatore);
        hackathon.setMentori(List.of(mentore));
        partecipazione = new Partecipazione(new Team("Alpha", leader), hackathon);
    }

    @Test
    void creaSegnalazione_shouldSave_whenMentorOfHackathon() {
        when(entityFinder.findUserOrThrow("mentore-1")).thenReturn(mentore);
        when(entityFinder.findPartecipazioneOrThrow("team-1", "hack-1")).thenReturn(partecipazione);
        when(unitOfWork.segnalazioneRepository()).thenReturn(segnalazioneRepository);

        var dto = service.creaSegnalazione(new CreaSegnalazioneRequest("hack-1", "team-1", "Comportamento scorretto"), "mentore-1");

        verify(segnalazioneRepository).save(any(Segnalazione.class));
        assertEquals("Comportamento scorretto", dto.getDescrizione());
    }

    @Test
    void creaSegnalazione_shouldThrow_whenNotMentorOfHackathon() {
        when(entityFinder.findUserOrThrow("org-1")).thenReturn(organizzatore);
        when(entityFinder.findPartecipazioneOrThrow("team-1", "hack-1")).thenReturn(partecipazione);

        assertThrows(UnauthorizedOperationException.class, () ->
            service.creaSegnalazione(new CreaSegnalazioneRequest("hack-1", "team-1", "x"), "org-1"));
        verify(unitOfWork, never()).segnalazioneRepository();
    }

    @Test
    void getSegnalazioni_organizerSeesAll() {
        Segnalazione s = new Segnalazione(partecipazione, mentore, "x");
        when(entityFinder.findHackathonOrThrow("hack-1")).thenReturn(hackathon);
        when(unitOfWork.segnalazioneRepository()).thenReturn(segnalazioneRepository);
        when(segnalazioneRepository.findByPartecipazioneHackathonId("hack-1")).thenReturn(List.of(s));

        assertEquals(1, service.getSegnalazioni("hack-1", "org-1").size());
    }

    @Test
    void getSegnalazioni_outsiderSeesNothing() {
        Segnalazione s = new Segnalazione(partecipazione, mentore, "x");
        when(entityFinder.findHackathonOrThrow("hack-1")).thenReturn(hackathon);
        when(unitOfWork.segnalazioneRepository()).thenReturn(segnalazioneRepository);
        when(unitOfWork.teamRepository()).thenReturn(teamRepository);
        when(segnalazioneRepository.findByPartecipazioneHackathonId("hack-1")).thenReturn(List.of(s));
        when(teamRepository.findByMembriId("estraneo")).thenReturn(List.of());

        assertTrue(service.getSegnalazioni("hack-1", "estraneo").isEmpty());
    }
}
