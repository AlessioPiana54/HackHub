package Infrastructure.Repositories;

import java.util.List;
import Application.IRepositories.ISegnalazioneRepository;
import Core.POJO_Entities.Segnalazione;
import Infrastructure.Databases.InMemoryDatabase;

public class SegnalazioneRepository implements ISegnalazioneRepository {
    private InMemoryDatabase db;

    public SegnalazioneRepository(InMemoryDatabase db) {
        this.db = db;
    }

    @Override
    public void save(Segnalazione segnalazione) {
        db.saveSegnalazione(segnalazione);
    }

    @Override
    public Segnalazione findById(String id) {
        return db.getSegnalazione(id);
    }

    @Override
    public List<Segnalazione> findAll() {
        return db.getAllSegnalazioni();
    }

    @Override
    public List<Segnalazione> findByHackathonId(String hackathonId) {
        return db.getSegnalazioniByHackathon(hackathonId);
    }
}
