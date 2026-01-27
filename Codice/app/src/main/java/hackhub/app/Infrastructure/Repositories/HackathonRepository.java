package hackhub.app.Infrastructure.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import hackhub.app.Application.IRepositories.IHackathonRepository;
import hackhub.app.Core.POJO_Entities.Hackathon;

@Repository
public interface HackathonRepository extends JpaRepository<Hackathon, String>, IHackathonRepository {
}
