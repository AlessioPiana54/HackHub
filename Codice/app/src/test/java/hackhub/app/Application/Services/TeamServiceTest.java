package hackhub.app.Application.Services;

import hackhub.app.Application.IRepositories.ITeamRepository;
import hackhub.app.Application.IUnitOfWork.IUnitOfWork;
import hackhub.app.Application.Requests.CreaTeamRequest;
import hackhub.app.Core.Enums.Ruolo;
import hackhub.app.Core.POJO_Entities.Team;
import hackhub.app.Core.POJO_Entities.User;
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
        teamService = new TeamService(unitOfWork, entityFinder, authorizationChecker);
    }

    @Test
    void creaTeam_shouldThrow_whenUserHasTeam() {
        when(unitOfWork.teamRepository()).thenReturn(teamRepository);

        User user = new User();
        user.setId("u-1");
        user.setRuolo(Ruolo.MEMBRO_TEAM);

        when(entityFinder.findUserOrThrow("u-1")).thenReturn(user);
        when(teamRepository.existsByNomeTeam("TeamX")).thenReturn(false);

        SecurityException ex = assertThrows(
                SecurityException.class,
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
        doThrow(new SecurityException("L'utente non fa parte di questo Team."))
                .when(authorizationChecker)
                .validateUserInTeam(any(Team.class), eq("user-2"), anyString());

        SecurityException ex = assertThrows(
                SecurityException.class,
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

        SecurityException ex = assertThrows(
                SecurityException.class,
                () -> teamService.trasferisciLeadership("team-1", "newLeader", "notLeader")
        );
        assertEquals("Solo l'attuale Leader può trasferire la leadership.", ex.getMessage());
    }
}

