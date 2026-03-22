package hackhub.app.Application.Services.Interfaces;

import hackhub.app.Application.DTOs.ClassificaTeamDTO;
import hackhub.app.Application.DTOs.HackathonSummaryDTO;
import hackhub.app.Application.Requests.CreaHackathonRequest;
import hackhub.app.Core.POJO_Entities.Hackathon;
import hackhub.app.Core.POJO_Entities.Partecipazione;
import hackhub.app.Core.POJO_Entities.Team;

import java.util.List;

public interface IHackathonService {
    Hackathon creaHackathon(CreaHackathonRequest request, String organizzatoreId);
    void terminaFaseValutazione(String hackathonId, String giudiceId);
    List<ClassificaTeamDTO> getClassifica(String hackathonId, String requesterId);
    void proclamaVincitore(String hackathonId, String teamId, String organizzatoreId);
    Partecipazione iscriviTeamAHackathon(String hackathonId, String teamId, String leaderId);
    List<HackathonSummaryDTO> getPublicHackathons();
    List<HackathonSummaryDTO> getMyHackathons(String userId);
    List<HackathonSummaryDTO> getJudgeHackathons(String giudiceId);
    List<HackathonSummaryDTO> getMentorHackathons(String mentoreId);
    List<Team> getParticipants(String hackathonId);
    HackathonSummaryDTO getHackathonById(String hackathonId);
    void forceStartTestHackathon(String organizerId);
}
