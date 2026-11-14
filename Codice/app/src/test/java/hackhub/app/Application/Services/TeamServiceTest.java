package hackhub.app.Application.Services;

import hackhub.app.Application.Exceptions.BusinessRuleException;
import hackhub.app.Application.IRepositories.IInvitoRepository;
import hackhub.app.Application.IRepositories.IPartecipazioneRepository;
import hackhub.app.Application.IRepositories.ITeamRepository;
import hackhub.app.Application.IRepositories.IUserRepository;
import hackhub.app.Application.IUnitOfWork.IUnitOfWork;
import hackhub.app.Application.Requests.CreaTeamRequest;
import hackhub.app.Core.Enums.Ruolo;
import hackhub.app.Core.Enums.StatoHackathon;
import hackhub.app.Core.POJO_Entities.Hackathon;
import hackhub.app.Core.POJO_Entities.Partecipazione;
import hackhub.app.Core.POJO_Entities.Team;
import hackhub.app.Core.POJO_Entities.User;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock private IUnitOfWork unitOfWork;
    @Mock private ITeamRepository teamRepository;
    @Mock private EntityFinder entityFinder;
    @Mock private AuthorizationChecker authorizationChecker;

    private TeamService teamService;

    @BeforeEach
    void setUp() {
        teamService = new TeamService(unitOfWork, entityFinder, authorizationChecker, new TeamMapper());
    }

    @Test
    void creaTeam_shouldThrow_whenUserHasTeam() {
        when(unitOfWork.teamRepository()).thenReturn(teamRepository);

        User user = new User();
        user.setId("u-1");
        user.setRuolo(Ruolo.MEMBRO_TEAM);

        when(entityFinder.findUserOrThrow("u-1")).thenReturn(user);
        when(teamRepository.existsByNomeTeam("TeamX")).thenReturn(false);

        hackhub.app.Application.Exceptions.UnauthorizedOperationException ex = assertThrows(
                hackhub.app.Application.Exceptions.UnauthorizedOperationException.class,
                () -> teamService.creaTeam(new CreaTeamRequest("TeamX"), "u-1")
        );
        assertEquals("L'utente specificato non ha i permessi necessari.", ex.getMessage());
    }

    @Test
    void abbandonaTeam_shouldThrow_whenUserNotInTeam() {
        User leader = new User();
        leader.setId("leader-1");
        leader.setRuolo(Ruolo.LEADER_TEAM);

        Team team = new Team("Alpha", leader);

        User member = new User();
        member.setId("user-2");
        member.setRuolo(Ruolo.MEMBRO_TEAM);

        when(entityFinder.findTeamOrThrow("team-1")).thenReturn(team);
        when(entityFinder.findUserOrThrow("user-2")).thenReturn(member);
        doThrow(new hackhub.app.Application.Exceptions.UnauthorizedOperationException("L'utente non fa parte di questo Team."))
                .when(authorizationChecker)
                .validateUserInTeam(any(Team.class), eq("user-2"), anyString());

        hackhub.app.Application.Exceptions.UnauthorizedOperationException ex = assertThrows(
                hackhub.app.Application.Exceptions.UnauthorizedOperationException.class,
                () -> teamService.abbandonaTeam("team-1", "user-2")
        );
        assertEquals("L'utente non fa parte di questo Team.", ex.getMessage());
    }

    @Test
    void trasferisciLeadership_shouldThrow_whenNotLeader() {
        User leader = new User();
        leader.setId("leader-1");
        leader.setRuolo(Ruolo.LEADER_TEAM);

        Team team = new Team("Alpha", leader);
        when(entityFinder.findTeamOrThrow("team-1")).thenReturn(team);

        hackhub.app.Application.Exceptions.UnauthorizedOperationException ex = assertThrows(
                hackhub.app.Application.Exceptions.UnauthorizedOperationException.class,
                () -> teamService.trasferisciLeadership("team-1", "newLeader", "notLeader")
        );
        assertEquals("Solo l'attuale Leader può trasferire la leadership.", ex.getMessage());
    }

    // --- abbandonaTeam ---

    @Mock private IPartecipazioneRepository partecipazioneRepository;
    @Mock private IInvitoRepository invitoRepository;
    @Mock private IUserRepository userRepository;

    private User leader() {
        User leader = new User("Mario", "Rossi", "m@h.it", "h", Ruolo.LEADER_TEAM);
        leader.setId("leader-1");
        return leader;
    }

    private Hackathon hackathonIn(StatoHackathon stato) {
        Hackathon h = new Hackathon();
        h.setStato(stato);
        return h;
    }

    @Test
    void abbandonaTeam_shouldDeleteTeam_whenLeaderAloneAndNoParticipations() {
        User leader = leader();
        Team team = new Team("Alpha", leader);
        when(entityFinder.findTeamOrThrow("team-1")).thenReturn(team);
        when(entityFinder.findUserOrThrow("leader-1")).thenReturn(leader);
        when(unitOfWork.partecipazioneRepository()).thenReturn(partecipazioneRepository);
        when(unitOfWork.invitoRepository()).thenReturn(invitoRepository);
        when(unitOfWork.teamRepository()).thenReturn(teamRepository);
        when(unitOfWork.userRepository()).thenReturn(userRepository);
        when(partecipazioneRepository.findByTeamId(any())).thenReturn(List.of());
        when(invitoRepository.findByTeam(team)).thenReturn(List.of());

        teamService.abbandonaTeam("team-1", "leader-1");

        verify(teamRepository).deleteById(any());
        assertEquals(Ruolo.UTENTE_SENZA_TEAM, leader.getRuolo());
    }

    @Test
    void abbandonaTeam_shouldDissolveTeam_whenLeaderAloneWithPastParticipations() {
        User leader = leader();
        Team team = new Team("Alpha", leader);
        when(entityFinder.findTeamOrThrow("team-1")).thenReturn(team);
        when(entityFinder.findUserOrThrow("leader-1")).thenReturn(leader);
        when(unitOfWork.partecipazioneRepository()).thenReturn(partecipazioneRepository);
        when(unitOfWork.invitoRepository()).thenReturn(invitoRepository);
        when(unitOfWork.teamRepository()).thenReturn(teamRepository);
        when(unitOfWork.userRepository()).thenReturn(userRepository);
        when(partecipazioneRepository.findByTeamId(any()))
            .thenReturn(List.of(new Partecipazione(team, hackathonIn(StatoHackathon.CONCLUSO))));
        when(invitoRepository.findByTeam(team)).thenReturn(List.of());

        teamService.abbandonaTeam("team-1", "leader-1");

        // Il team resta per conservare sottomissioni e classifiche, ma senza membri
        verify(teamRepository, never()).deleteById(any());
        verify(teamRepository).save(team);
        assertNull(team.getLeaderSquadra());
        assertTrue(team.getMembri().isEmpty());
        assertEquals(Ruolo.UTENTE_SENZA_TEAM, leader.getRuolo());
    }

    @Test
    void abbandonaTeam_shouldThrow_whenLeaderAloneDuringActiveHackathon() {
        User leader = leader();
        Team team = new Team("Alpha", leader);
        when(entityFinder.findTeamOrThrow("team-1")).thenReturn(team);
        when(entityFinder.findUserOrThrow("leader-1")).thenReturn(leader);
        when(unitOfWork.partecipazioneRepository()).thenReturn(partecipazioneRepository);
        when(partecipazioneRepository.findByTeamId(any()))
            .thenReturn(List.of(new Partecipazione(team, hackathonIn(StatoHackathon.IN_CORSO))));

        assertThrows(BusinessRuleException.class, () -> teamService.abbandonaTeam("team-1", "leader-1"));
        assertEquals(Ruolo.LEADER_TEAM, leader.getRuolo());
    }

    @Test
    void abbandonaTeam_shouldThrow_whenLeaderHasOtherMembers() {
        User leader = leader();
        User membro = new User("Anna", "Neri", "a@h.it", "h", Ruolo.MEMBRO_TEAM);
        membro.setId("membro-1");
        Team team = new Team("Alpha", leader);
        team.getMembri().add(membro);
        when(entityFinder.findTeamOrThrow("team-1")).thenReturn(team);
        when(entityFinder.findUserOrThrow("leader-1")).thenReturn(leader);

        assertThrows(BusinessRuleException.class, () -> teamService.abbandonaTeam("team-1", "leader-1"));
    }

    @Test
    void abbandonaTeam_shouldRemoveMember_whenNotLeader() {
        User leader = leader();
        User membro = new User("Anna", "Neri", "a@h.it", "h", Ruolo.MEMBRO_TEAM);
        membro.setId("membro-1");
        Team team = new Team("Alpha", leader);
        team.getMembri().add(membro);
        when(entityFinder.findTeamOrThrow("team-1")).thenReturn(team);
        when(entityFinder.findUserOrThrow("membro-1")).thenReturn(membro);
        when(unitOfWork.teamRepository()).thenReturn(teamRepository);
        when(unitOfWork.userRepository()).thenReturn(userRepository);

        teamService.abbandonaTeam("team-1", "membro-1");

        assertEquals(1, team.getMembri().size());
        assertEquals(Ruolo.UTENTE_SENZA_TEAM, membro.getRuolo());
    }
}

