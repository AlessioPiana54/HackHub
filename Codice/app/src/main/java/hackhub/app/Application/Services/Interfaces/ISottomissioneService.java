package hackhub.app.Application.Services.Interfaces;

import hackhub.app.Application.Requests.CreaValutazioneRequest;
import hackhub.app.Application.Requests.InviaSottomissioneRequest;
import hackhub.app.Application.Requests.ModificaSottomissioneRequest;
import hackhub.app.Core.POJO_Entities.Sottomissione;
import hackhub.app.Core.POJO_Entities.Valutazione;

import java.util.List;

public interface ISottomissioneService {
    Sottomissione inviaSottomissione(InviaSottomissioneRequest request, String userId);
    Valutazione valutaSottomissione(CreaValutazioneRequest request, String giudiceId, String sottomissioneId);
    Sottomissione modificaSottomissione(ModificaSottomissioneRequest request, String userId, String sottomissioneId);
    List<Sottomissione> getTeamSubmissions(String userId);
    List<Sottomissione> getSubmissionsByHackathon(String hackathonId);
}
