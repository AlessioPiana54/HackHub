package Infrastructure.Repositories;

import Infrastructure.Databases.InMemoryDatabase;

import java.util.List;

import Application.IRepositories.IHackathonRepository;
import Core.POJO_Entities.Hackathon;

public class HackathonRepository implements IHackathonRepository {
    private InMemoryDatabase db;

    public HackathonRepository(InMemoryDatabase db) {
        this.db = db;
    }

    @Override
    public void save(Hackathon hackathon) {
        db.saveHackathon(hackathon);
    }

    @Override
    public void edit(Hackathon hackathon) {
        db.editHackathon(hackathon);
    }

    @Override
    public Hackathon findById(String id) {
        return db.getHackathon(id);
    }

    @Override
    public List<Hackathon> findAll() {
        return db.getAllHackathons();
    }

    @Override
    public void deleteById(String id) {
        db.deleteHackathon(id);
    }
}
