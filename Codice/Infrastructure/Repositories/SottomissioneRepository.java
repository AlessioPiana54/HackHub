package Infrastructure.Repositories;

import Application.IRepositories.ISottomissioneRepository;
import Core.POJO_Entities.Sottomissione;
import Infrastructure.Databases.InMemoryDatabase;
import java.util.List;

public class SottomissioneRepository implements ISottomissioneRepository {
    private InMemoryDatabase db;

    public SottomissioneRepository(InMemoryDatabase db) {
        this.db = db;
    }

    @Override
    public void save(Sottomissione sottomissione) {
        db.salvaSottomissione(sottomissione);
    }

    @Override
    public void edit(Sottomissione sottomissione) {
        db.editSottomissione(sottomissione);
    }

    @Override
    public Sottomissione findById(String id) {
        return db.getSottomissione(id);
    }

    @Override
    public List<Sottomissione> findAll() {
        return db.getAllSottomissioni();
    }

    @Override
    public List<Sottomissione> findByHackathonId(String idHackathon) {
        return db.getSottomissioniByHackathon(idHackathon);
    }

    @Override
    public List<Sottomissione> findByTeamId(String idTeam) {
        return db.getSottomissioniByTeam(idTeam);
    }

    @Override
    public void deleteById(String id) {
        db.deleteSottomissione(id);
    }
}
