package hackhub.app.Application.Services.Interfaces;

import hackhub.app.Application.DTOs.SottomissioneDTO;
import hackhub.app.Application.Requests.CreaValutazioneRequest;
import hackhub.app.Application.Requests.InviaSottomissioneRequest;
import hackhub.app.Application.Requests.ModificaSottomissioneRequest;
import hackhub.app.Application.DTOs.ValutazioneDTO;

import java.util.List;

public interface ISottomissioneService {
    SottomissioneDTO inviaSottomissione(InviaSottomissioneRequest request, String userId);
    ValutazioneDTO valutaSottomissione(CreaValutazioneRequest request, String giudiceId, String sottomissioneId);
    SottomissioneDTO modificaSottomissione(ModificaSottomissioneRequest request, String userId, String sottomissioneId);
    List<SottomissioneDTO> getTeamSubmissions(String userId);
    List<SottomissioneDTO> getSubmissionsByHackathon(String hackathonId, String requesterId);
}
