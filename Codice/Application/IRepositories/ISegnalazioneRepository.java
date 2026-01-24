package Application.IRepositories;

import java.util.List;
import Core.POJO_Entities.Segnalazione;

public interface ISegnalazioneRepository {
    void save(Segnalazione segnalazione);

    Segnalazione findById(String id);

    List<Segnalazione> findAll();

    List<Segnalazione> findByHackathonId(String hackathonId);
}
