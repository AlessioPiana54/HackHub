package hackhub.app.Application.Services;

import hackhub.app.Application.Exceptions.BusinessRuleException;
import hackhub.app.Application.Exceptions.UnauthorizedOperationException;
import hackhub.app.Application.IRepositories.ISottomissioneRepository;
import hackhub.app.Application.IRepositories.IValutazioneRepository;
import hackhub.app.Application.IUnitOfWork.IUnitOfWork;
import hackhub.app.Application.Requests.CreaValutazioneRequest;
import hackhub.app.Application.Requests.InviaSottomissioneRequest;
import hackhub.app.Application.Strategies.LinkStrategyContext;
import hackhub.app.Core.Enums.Ruolo;
import hackhub.app.Core.Enums.StatoHackathon;
import hackhub.app.Core.POJO_Entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SottomissioneServiceTest {

    @Mock private IUnitOfWork unitOfWork;
    @Mock private ISottomissioneRepository sottomissioneRepository;
    @Mock private IValutazioneRepository valutazioneRepository;
    @Mock private EntityFinder entityFinder;
    @Mock private AuthorizationChecker authorizationChecker;
    @Mock private LinkStrategyContext linkStrategyContext;

    private SottomissioneService sottomissioneService;

    private User leader;
    private Team team;
    private Hackathon hackathon;
    private Partecipazione partecipazione;

    @BeforeEach
    void setUp() {
        sottomissioneService = new SottomissioneService(unitOfWork, entityFinder, authorizationChecker, linkStrategyContext);

        leader = new User("Mario", "Rossi", "mario@hackhub.it", "hash", Ruolo.LEADER_TEAM);
        leader.setId("leader-1");
        team = new Team("Alpha", leader);

        hackathon = new Hackathon();
        hackathon.setGiudice(leader); // il leader funge anche da giudice nei test

        partecipazione = new Partecipazione(team, hackathon);
    }

    // --- inviaSottomissione ---

    @Test
    void inviaSottomissione_shouldThrow_whenHackathonNotInCorso() {
        hackathon.setStato(StatoHackathon.IN_ISCRIZIONE);
        when(entityFinder.findTeamOrThrow("team-1")).thenReturn(team);
        when(entityFinder.findHackathonOrThrow("hack-1")).thenReturn(hackathon);

        InviaSottomissioneRequest request = new InviaSottomissioneRequest(
            "hack-1", "team-1", "https://github.com/test/repo", "Progetto test"
        );

        BusinessRuleException ex = assertThrows(
            BusinessRuleException.class,
            () -> sottomissioneService.inviaSottomissione(request, "leader-1")
        );
        assertEquals("Le sottomissioni sono accettate solo durante l'hackathon.", ex.getMessage());
        verify(unitOfWork, never()).sottomissioneRepository();
    }

    @Test
    void inviaSottomissione_shouldThrow_whenTeamAlreadySubmitted() {
        hackathon.setStato(StatoHackathon.IN_CORSO);
        when(entityFinder.findTeamOrThrow("team-1")).thenReturn(team);
        when(entityFinder.findHackathonOrThrow("hack-1")).thenReturn(hackathon);
        when(entityFinder.findPartecipazioneOrThrow("team-1", "hack-1")).thenReturn(partecipazione);
        when(unitOfWork.sottomissioneRepository()).thenReturn(sottomissioneRepository);
        when(sottomissioneRepository.existsByPartecipazione_Hackathon_IdAndPartecipazione_Team_Id("hack-1", "team-1"))
            .thenReturn(true);

        InviaSottomissioneRequest request = new InviaSottomissioneRequest(
            "hack-1", "team-1", "https://github.com/test/repo", "Progetto test"
        );

        BusinessRuleException ex = assertThrows(
            BusinessRuleException.class,
            () -> sottomissioneService.inviaSottomissione(request, "leader-1")
        );
        assertEquals("Il team ha già inviato una sottomissione per questo hackathon.", ex.getMessage());
    }

    @Test
    void inviaSottomissione_shouldThrow_whenUserNotInTeam() {
        hackathon.setStato(StatoHackathon.IN_CORSO);
        when(entityFinder.findTeamOrThrow("team-1")).thenReturn(team);
        when(entityFinder.findHackathonOrThrow("hack-1")).thenReturn(hackathon);
        doThrow(new UnauthorizedOperationException("Solo i membri del team possono inviare sottomissioni."))
            .when(authorizationChecker)
            .validateUserInTeam(eq(team), eq("estraneo-1"), anyString());

        InviaSottomissioneRequest request = new InviaSottomissioneRequest(
            "hack-1", "team-1", "https://github.com/test/repo", "Progetto test"
        );

        UnauthorizedOperationException ex = assertThrows(
            UnauthorizedOperationException.class,
            () -> sottomissioneService.inviaSottomissione(request, "estraneo-1")
        );
        assertEquals("Solo i membri del team possono inviare sottomissioni.", ex.getMessage());
    }

    // --- valutaSottomissione ---

    @Test
    void valutaSottomissione_shouldThrow_whenHackathonNotInValutazione() {
        hackathon.setStato(StatoHackathon.IN_CORSO);
        User mittente = new User();
        mittente.setId("m-1");
        Sottomissione sottomissione = new Sottomissione(partecipazione, mittente, "https://github.com/test/repo", "desc");
        when(entityFinder.findSottomissioneOrThrow("sub-1")).thenReturn(sottomissione);

        BusinessRuleException ex = assertThrows(
            BusinessRuleException.class,
            () -> sottomissioneService.valutaSottomissione(new CreaValutazioneRequest(8.0, "Buono"), "leader-1", "sub-1")
        );
        assertEquals("L'Hackathon non è in fase di valutazione.", ex.getMessage());
    }

    @Test
    void valutaSottomissione_shouldThrow_whenNotAssignedJudge() {
        hackathon.setStato(StatoHackathon.IN_VALUTAZIONE);
        // hackathon.getGiudice().getId() == "leader-1", ma passiamo "wrong-judge"
        User mittente = new User();
        mittente.setId("m-1");
        Sottomissione sottomissione = new Sottomissione(partecipazione, mittente, "https://github.com/test/repo", "desc");
        when(entityFinder.findSottomissioneOrThrow("sub-1")).thenReturn(sottomissione);

        UnauthorizedOperationException ex = assertThrows(
            UnauthorizedOperationException.class,
            () -> sottomissioneService.valutaSottomissione(new CreaValutazioneRequest(8.0, "Buono"), "wrong-judge", "sub-1")
        );
        assertEquals("Solo il giudice dell'Hackathon può valutare le sottomissioni.", ex.getMessage());
    }

    @Test
    void valutaSottomissione_shouldThrow_whenAlreadyEvaluated() {
        hackathon.setStato(StatoHackathon.IN_VALUTAZIONE);
        // giudiceId == "leader-1" → passa il controllo di autorizzazione
        User mittente = new User();
        mittente.setId("m-1");
        Sottomissione sottomissione = new Sottomissione(partecipazione, mittente, "https://github.com/test/repo", "desc");
        when(entityFinder.findSottomissioneOrThrow("sub-1")).thenReturn(sottomissione);
        when(entityFinder.findUserOrThrow("leader-1")).thenReturn(leader);
        when(unitOfWork.valutazioneRepository()).thenReturn(valutazioneRepository);
        when(valutazioneRepository.existsBySottomissioneId(isNull())).thenReturn(true);

        BusinessRuleException ex = assertThrows(
            BusinessRuleException.class,
            () -> sottomissioneService.valutaSottomissione(new CreaValutazioneRequest(8.0, "Buono"), "leader-1", "sub-1")
        );
        assertEquals("Questa sottomissione è già stata valutata.", ex.getMessage());
    }

    @Test
    void valutaSottomissione_shouldSaveValutazione_whenValid() {
        hackathon.setStato(StatoHackathon.IN_VALUTAZIONE);
        User mittente = new User();
        mittente.setId("m-1");
        Sottomissione sottomissione = new Sottomissione(partecipazione, mittente, "https://github.com/test/repo", "desc");
        when(entityFinder.findSottomissioneOrThrow("sub-1")).thenReturn(sottomissione);
        when(entityFinder.findUserOrThrow("leader-1")).thenReturn(leader);
        when(unitOfWork.valutazioneRepository()).thenReturn(valutazioneRepository);
        when(valutazioneRepository.existsBySottomissioneId(isNull())).thenReturn(false);
        when(valutazioneRepository.save(any(Valutazione.class))).thenAnswer(inv -> inv.getArgument(0));

        Valutazione result = sottomissioneService.valutaSottomissione(
            new CreaValutazioneRequest(9.0, "Ottimo lavoro"), "leader-1", "sub-1"
        );

        verify(valutazioneRepository).save(any(Valutazione.class));
        assertEquals(9.0, result.getVoto());
        assertEquals("Ottimo lavoro", result.getGiudizio());
    }
}
