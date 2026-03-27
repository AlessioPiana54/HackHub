package hackhub.app.Application.Services.Interfaces;

import hackhub.app.Application.DTOs.InvitoDTO;
import hackhub.app.Application.Requests.CreaInvitoRequest;
import hackhub.app.Application.Requests.RispostaInvitoRequest;

import java.util.List;

public interface IInvitoService {
    InvitoDTO inviaInvito(CreaInvitoRequest request, String mittenteId);
    void gestisciRisposta(RispostaInvitoRequest request, String userId, String invitoId);
    List<InvitoDTO> getReceivedInvitations(String userId);
    List<InvitoDTO> getSentInvitations(String userId);
}
