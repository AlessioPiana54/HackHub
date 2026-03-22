package hackhub.app.Application.Services.Interfaces;

import hackhub.app.Application.DTOs.InvitoDTO;
import hackhub.app.Application.Requests.CreaInvitoRequest;
import hackhub.app.Application.Requests.RispostaInvitoRequest;
import hackhub.app.Core.POJO_Entities.Invito;

import java.util.List;

public interface IInvitoService {
    Invito inviaInvito(CreaInvitoRequest request, String mittenteId);
    void gestisciRisposta(RispostaInvitoRequest request, String userId, String invitoId);
    List<InvitoDTO> getReceivedInvitations(String userId);
    List<InvitoDTO> getSentInvitations(String userId);
}
