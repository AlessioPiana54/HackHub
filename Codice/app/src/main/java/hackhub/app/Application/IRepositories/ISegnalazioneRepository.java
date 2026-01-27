package hackhub.app.Application.IRepositories;

import hackhub.app.Core.POJO_Entities.Segnalazione;
import java.util.List;

public interface ISegnalazioneRepository {
    List<Segnalazione> findByPartecipazioneHackathonId(String hackathonId);

    Segnalazione save(Segnalazione segnalazione);

    java.util.Optional<Segnalazione> findById(String id);
}
