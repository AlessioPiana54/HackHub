package hackhub.app.Application.Services.Interfaces;

import hackhub.app.Application.DTOs.TeamDTO;
import hackhub.app.Application.Requests.CreaTeamRequest;
import hackhub.app.Application.Requests.UpdateTeamRequest;
import hackhub.app.Core.POJO_Entities.Partecipazione;
import hackhub.app.Core.POJO_Entities.Team;

import java.util.List;

public interface ITeamService {
    void cleanupOrphanedTeams();
    Team creaTeam(CreaTeamRequest request, String leaderId);
    Team updateTeam(String teamId, UpdateTeamRequest request, String leaderId);
    Partecipazione iscriviTeam(String teamId, String hackathonId, String richiedenteId);
    void abbandonaTeam(String teamId, String memberId);
    Team trasferisciLeadership(String teamId, String newLeaderId, String currentLeaderId);
    List<TeamDTO> getUserTeams(String userId);
    TeamDTO getTeamDetails(String teamId);
}
