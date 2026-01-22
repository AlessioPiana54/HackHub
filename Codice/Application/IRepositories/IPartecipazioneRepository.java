package Application.IRepositories;

import Core.POJO_Entities.Partecipazione;
import java.util.List;

public interface IPartecipazioneRepository {
    void save(Partecipazione partecipazione);

    Partecipazione findById(String id);

    List<Partecipazione> findAll();

    List<Partecipazione> findByTeamId(String teamId);

    List<Partecipazione> findByHackathonId(String hackathonId);

    void deleteById(String id);
}
