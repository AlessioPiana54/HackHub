package hackhub.app.Application.Services.Interfaces;

import hackhub.app.Application.DTOs.SegnalazioneDTO;
import hackhub.app.Application.Requests.CreaSegnalazioneRequest;
import hackhub.app.Core.POJO_Entities.Segnalazione;

import java.util.List;

public interface ISegnalazioneService {
    Segnalazione creaSegnalazione(CreaSegnalazioneRequest request, String mentoreId);
    List<SegnalazioneDTO> getSegnalazioni(String idHackathon, String idUser);
}
