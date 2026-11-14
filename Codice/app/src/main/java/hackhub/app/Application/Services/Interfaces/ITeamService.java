package hackhub.app.Application.Services.Interfaces;

import hackhub.app.Application.DTOs.TeamDTO;
import hackhub.app.Application.Requests.CreaTeamRequest;
import hackhub.app.Application.Requests.UpdateTeamRequest;

import java.util.List;

public interface ITeamService {
    void cleanupOrphanedTeams();
    TeamDTO creaTeam(CreaTeamRequest request, String leaderId);
    TeamDTO updateTeam(String teamId, UpdateTeamRequest request, String leaderId);
    void abbandonaTeam(String teamId, String memberId);
    TeamDTO trasferisciLeadership(String teamId, String newLeaderId, String currentLeaderId);
    List<TeamDTO> getUserTeams(String userId);
    TeamDTO getTeamDetails(String teamId);
}
