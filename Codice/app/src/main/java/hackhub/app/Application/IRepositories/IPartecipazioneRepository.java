package hackhub.app.Application.IRepositories;

import hackhub.app.Core.POJO_Entities.Partecipazione;
import java.util.List;
import java.util.Optional;

public interface IPartecipazioneRepository {
    List<Partecipazione> findByTeamId(String teamId);

    List<Partecipazione> findByHackathonId(String hackathonId);

    Optional<Partecipazione> findByTeamIdAndHackathonId(String teamId, String hackathonId);

    Partecipazione save(Partecipazione partecipazione);

    Optional<Partecipazione> findById(String id);
}
