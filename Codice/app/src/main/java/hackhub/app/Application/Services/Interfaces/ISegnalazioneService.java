package hackhub.app.Application.Services.Interfaces;

import hackhub.app.Application.DTOs.SegnalazioneDTO;
import hackhub.app.Application.Requests.CreaSegnalazioneRequest;

import java.util.List;

public interface ISegnalazioneService {
    SegnalazioneDTO creaSegnalazione(CreaSegnalazioneRequest request, String mentoreId);
    List<SegnalazioneDTO> getSegnalazioni(String idHackathon, String idUser);
}
