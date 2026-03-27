package hackhub.app.Application.Services;

import hackhub.app.Application.Exceptions.BusinessRuleException;
import hackhub.app.Application.Exceptions.EntityNotFoundException;
import hackhub.app.Application.Exceptions.UnauthorizedOperationException;
import hackhub.app.Application.IRepositories.IInvitoRepository;
import hackhub.app.Application.IRepositories.IPartecipazioneRepository;
import hackhub.app.Application.IRepositories.IUserRepository;
import hackhub.app.Application.IUnitOfWork.IUnitOfWork;
import hackhub.app.Application.Requests.CreaInvitoRequest;
import hackhub.app.Application.Requests.RispostaInvitoRequest;
import hackhub.app.Core.Enums.Ruolo;
import hackhub.app.Core.Enums.StatoHackathon;
import hackhub.app.Core.POJO_Entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvitoServiceTest {

    @Mock private IUnitOfWork unitOfWork;
    @Mock private IInvitoRepository invitoRepository;
    @Mock private IUserRepository userRepository;
    @Mock private IPartecipazioneRepository partecipazioneRepository;
    @Mock private EntityFinder entityFinder;
    @Mock private AuthorizationChecker authorizationChecker;

    private InvitoService invitoService;

    @BeforeEach
    void setUp() {
        invitoService = new InvitoService(unitOfWork, entityFinder, authorizationChecker);
    }

    // --- inviaInvito ---

    @Test
    void inviaInvito_shouldThrow_whenDestinatarioNotFound() {
        User leader = new User("Mario", "Rossi", "mario@hackhub.it", "hash", Ruolo.LEADER_TEAM);
        leader.setId("leader-1");
        Team team = new Team("Alpha", leader);

        when(entityFinder.findTeamOrThrow("team-1")).thenReturn(team);
        when(entityFinder.findUserOrThrow("leader-1")).thenReturn(leader);
        when(unitOfWork.userRepository()).thenReturn(userRepository);
        when(userRepository.findByEmail("notfound@test.it")).thenReturn(null);

        EntityNotFoundException ex = assertThrows(
            EntityNotFoundException.class,
            () -> invitoService.inviaInvito(new CreaInvitoRequest("team-1", "notfound@test.it"), "leader-1")
        );
        assertEquals("Destinatario non trovato", ex.getMessage());
    }

    @Test
    void inviaInvito_shouldThrow_whenDestinatarioHasTeam() {
        User leader = new User("Mario", "Rossi", "mario@hackhub.it", "hash", Ruolo.LEADER_TEAM);
        leader.setId("leader-1");
        Team team = new Team("Alpha", leader);

        User destinatario = new User("Luca", "Verdi", "luca@test.it", "hash", Ruolo.MEMBRO_TEAM);
        destinatario.setId("dest-1");

        when(entityFinder.findTeamOrThrow("team-1")).thenReturn(team);
        when(entityFinder.findUserOrThrow("leader-1")).thenReturn(leader);
        when(unitOfWork.userRepository()).thenReturn(userRepository);
        when(userRepository.findByEmail("luca@test.it")).thenReturn(destinatario);
        doThrow(new UnauthorizedOperationException("L'utente ha già un team o un ruolo incompatibile."))
            .when(authorizationChecker)
            .validateUserRole(eq(destinatario), eq(Ruolo.UTENTE_SENZA_TEAM), anyString());

        UnauthorizedOperationException ex = assertThrows(
            UnauthorizedOperationException.class,
            () -> invitoService.inviaInvito(new CreaInvitoRequest("team-1", "luca@test.it"), "leader-1")
        );
        assertEquals("L'utente ha già un team o un ruolo incompatibile.", ex.getMessage());
        verify(unitOfWork, never()).invitoRepository();
    }

    @Test
    void inviaInvito_shouldThrow_whenSenderNotInTeam() {
        User leader = new User("Mario", "Rossi", "mario@hackhub.it", "hash", Ruolo.LEADER_TEAM);
        leader.setId("leader-1");
        Team team = new Team("Alpha", leader);

        User estraneo = new User("Estraneo", "X", "estraneo@test.it", "hash", Ruolo.UTENTE_SENZA_TEAM);
        estraneo.setId("estr-1");

        User destinatario = new User("Luca", "Verdi", "luca@test.it", "hash", Ruolo.UTENTE_SENZA_TEAM);
        destinatario.setId("dest-1");

        when(entityFinder.findTeamOrThrow("team-1")).thenReturn(team);
        when(entityFinder.findUserOrThrow("estr-1")).thenReturn(estraneo);
        when(unitOfWork.userRepository()).thenReturn(userRepository);
        when(userRepository.findByEmail("luca@test.it")).thenReturn(destinatario);
        doThrow(new UnauthorizedOperationException("Solo i membri del team o il leader possono inviare inviti."))
            .when(authorizationChecker)
            .validateUserInTeam(eq(team), eq("estr-1"), anyString());

        UnauthorizedOperationException ex = assertThrows(
            UnauthorizedOperationException.class,
            () -> invitoService.inviaInvito(new CreaInvitoRequest("team-1", "luca@test.it"), "estr-1")
        );
        assertEquals("Solo i membri del team o il leader possono inviare inviti.", ex.getMessage());
    }

    // --- gestisciRisposta ---

    @Test
    void gestisciRisposta_shouldThrow_whenInvitoNotFound() {
        when(unitOfWork.invitoRepository()).thenReturn(invitoRepository);
        when(invitoRepository.findById("inv-999")).thenReturn(Optional.empty());

        assertThrows(
            EntityNotFoundException.class,
            () -> invitoService.gestisciRisposta(new RispostaInvitoRequest(false), "u-1", "inv-999")
        );
    }

    @Test
    void gestisciRisposta_shouldThrow_whenInvitoNotForUser() {
        User mittente = new User();
        mittente.setId("m-1");
        User destinatario = new User();
        destinatario.setId("dest-1");
        Team team = new Team("Alpha", mittente);
        Invito invito = new Invito(team, destinatario, mittente);

        User altroUser = new User();
        altroUser.setId("altro-1");

        when(unitOfWork.invitoRepository()).thenReturn(invitoRepository);
        when(invitoRepository.findById("inv-1")).thenReturn(Optional.of(invito));
        when(entityFinder.findUserOrThrow("altro-1")).thenReturn(altroUser);

        UnauthorizedOperationException ex = assertThrows(
            UnauthorizedOperationException.class,
            () -> invitoService.gestisciRisposta(new RispostaInvitoRequest(false), "altro-1", "inv-1")
        );
        assertEquals("Questo invito non è per te.", ex.getMessage());
    }

    @Test
    void gestisciRisposta_shouldDeleteInvito_whenRifiutato() {
        User mittente = new User();
        mittente.setId("m-1");
        User destinatario = new User();
        destinatario.setId("dest-1");
        Team team = new Team("Alpha", mittente);
        Invito invito = new Invito(team, destinatario, mittente);

        when(unitOfWork.invitoRepository()).thenReturn(invitoRepository);
        when(invitoRepository.findById("inv-1")).thenReturn(Optional.of(invito));
        when(entityFinder.findUserOrThrow("dest-1")).thenReturn(destinatario);

        invitoService.gestisciRisposta(new RispostaInvitoRequest(false), "dest-1", "inv-1");

        verify(invitoRepository).delete(invito);
    }

    @Test
    void gestisciRisposta_shouldThrow_whenTeamInActiveHackathon() {
        User mittente = new User();
        mittente.setId("m-1");
        User destinatario = new User();
        destinatario.setId("dest-1");
        destinatario.setRuolo(Ruolo.UTENTE_SENZA_TEAM);
        Team team = new Team("Alpha", mittente);
        Invito invito = new Invito(team, destinatario, mittente);

        // Il team è già iscritto a un hackathon attivo
        Hackathon hackathonAttivo = new Hackathon();
        hackathonAttivo.setStato(StatoHackathon.IN_CORSO);
        Partecipazione partecipazione = new Partecipazione(team, hackathonAttivo);

        when(unitOfWork.invitoRepository()).thenReturn(invitoRepository);
        when(invitoRepository.findById("inv-1")).thenReturn(Optional.of(invito));
        when(entityFinder.findUserOrThrow("dest-1")).thenReturn(destinatario);
        when(unitOfWork.partecipazioneRepository()).thenReturn(partecipazioneRepository);
        when(partecipazioneRepository.findByTeamId(nullable(String.class))).thenReturn(List.of(partecipazione));

        BusinessRuleException ex = assertThrows(
            BusinessRuleException.class,
            () -> invitoService.gestisciRisposta(new RispostaInvitoRequest(true), "dest-1", "inv-1")
        );
        assertTrue(ex.getMessage().contains("Hackathon attivo"));
    }

    @Test
    void gestisciRisposta_shouldAcceptInvito_whenNoActiveHackathon() {
        User mittente = new User();
        mittente.setId("m-1");
        User destinatario = new User();
        destinatario.setId("dest-1");
        destinatario.setRuolo(Ruolo.UTENTE_SENZA_TEAM);
        Team team = new Team("Alpha", mittente);
        Invito invito = new Invito(team, destinatario, mittente);

        when(unitOfWork.invitoRepository()).thenReturn(invitoRepository);
        when(invitoRepository.findById("inv-1")).thenReturn(Optional.of(invito));
        when(entityFinder.findUserOrThrow("dest-1")).thenReturn(destinatario);
        when(unitOfWork.partecipazioneRepository()).thenReturn(partecipazioneRepository);
        when(partecipazioneRepository.findByTeamId(nullable(String.class))).thenReturn(List.of());
        when(unitOfWork.userRepository()).thenReturn(userRepository);
        when(unitOfWork.teamRepository()).thenReturn(mock(hackhub.app.Application.IRepositories.ITeamRepository.class));

        invitoService.gestisciRisposta(new RispostaInvitoRequest(true), "dest-1", "inv-1");

        // L'utente deve essere aggiunto al team
        assertTrue(team.getMembri().contains(destinatario));
        assertEquals(Ruolo.MEMBRO_TEAM, destinatario.getRuolo());
        verify(invitoRepository).delete(invito);
    }
}
