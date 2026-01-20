package Application.IRepositories;

import java.util.List;

import Core.POJO_Entities.Team;

public interface ITeamRepository {
    void save(Team team);

    void edit(Team team);

    Team findById(String id);

    List<Team> findAll();

    void deleteById(String id);

    boolean existsByName(String name);
}
