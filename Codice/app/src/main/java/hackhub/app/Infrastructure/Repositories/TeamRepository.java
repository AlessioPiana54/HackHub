package hackhub.app.Infrastructure.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import hackhub.app.Application.IRepositories.ITeamRepository;
import hackhub.app.Core.POJO_Entities.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, String>, ITeamRepository {
}
