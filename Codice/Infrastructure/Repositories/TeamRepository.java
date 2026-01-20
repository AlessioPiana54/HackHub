package Infrastructure.Repositories;

import java.util.List;

import Application.IRepositories.ITeamRepository;
import Core.POJO_Entities.Team;
import Infrastructure.Databases.InMemoryDatabase;

public class TeamRepository implements ITeamRepository {
    private InMemoryDatabase db;

    public TeamRepository(InMemoryDatabase db) {
        this.db = db;
    }

    @Override
    public void save(Team team) {
        db.saveTeam(team);
    }

    @Override
    public void edit(Team team) {
        db.editTeam(team);
    }

    @Override
    public Team findById(String id) {
        return db.getTeam(id);
    }

    @Override
    public List<Team> findAll() {
        return db.getAllTeams();
    }

    @Override
    public void deleteById(String id) {
        db.deleteTeam(id);
    }

    @Override
    public boolean existsByName(String name) {
        return db.getAllTeams().stream()
                .anyMatch(t -> t.getNomeTeam().equalsIgnoreCase(name));
    }
}
