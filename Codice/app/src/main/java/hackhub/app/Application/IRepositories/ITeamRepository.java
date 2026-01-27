package hackhub.app.Application.IRepositories;

import hackhub.app.Core.POJO_Entities.Team;

public interface ITeamRepository {
    boolean existsByNomeTeam(String nomeTeam);

    Team save(Team team);

    java.util.Optional<Team> findById(String id);
}
