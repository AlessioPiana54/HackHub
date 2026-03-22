package hackhub.app.Application.Services.Interfaces;

import hackhub.app.Application.DTOs.RichiestaSupportoDTO;
import hackhub.app.Application.Requests.CreaRichiestaSupportoRequest;
import hackhub.app.Application.Requests.ProponiCallRequest;
import hackhub.app.Core.POJO_Entities.RichiestaSupporto;

import java.util.List;

public interface IRichiestaSupportoService {
    void creaRichiestaSupporto(CreaRichiestaSupportoRequest request, String richiedenteId);
    List<RichiestaSupportoDTO> getRichiestePerMentore(String hackathonId, String mentorId);
    RichiestaSupporto proponiCall(ProponiCallRequest request, String mentorId, String richiestaId);
    List<RichiestaSupportoDTO> getRichiesteGestitePerTeam(String hackathonId, String teamId, String userId);
}
