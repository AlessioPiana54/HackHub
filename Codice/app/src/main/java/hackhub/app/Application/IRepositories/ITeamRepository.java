package hackhub.app.Application.IRepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import hackhub.app.Core.POJO_Entities.Team;

@Repository
public interface ITeamRepository extends JpaRepository<Team, String> {
    boolean existsByNomeTeam(String nomeTeam);
}
