package Infrastructure.Repositories;

import Application.IRepositories.IPartecipazioneRepository;
import Core.POJO_Entities.Partecipazione;
import Infrastructure.Databases.InMemoryDatabase;
import java.util.List;

public class PartecipazioneRepository implements IPartecipazioneRepository {
    private InMemoryDatabase db;

    public PartecipazioneRepository(InMemoryDatabase db) {
        this.db = db;
    }

    @Override
    public void save(Partecipazione partecipazione) {
        db.savePartecipazione(partecipazione);
    }

    @Override
    public Partecipazione findById(String id) {
        return db.getPartecipazione(id);
    }

    @Override
    public List<Partecipazione> findAll() {
        return db.getAllPartecipazioni();
    }

    @Override
    public List<Partecipazione> findByTeamId(String teamId) {
        return db.getPartecipazioniByTeam(teamId);
    }

    @Override
    public List<Partecipazione> findByHackathonId(String hackathonId) {
        return db.getPartecipazioniByHackathon(hackathonId);
    }

    @Override
    public void deleteById(String id) {
        db.deletePartecipazione(id);
    }
}
