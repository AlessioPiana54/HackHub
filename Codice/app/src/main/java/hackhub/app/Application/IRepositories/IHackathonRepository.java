package hackhub.app.Application.IRepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import hackhub.app.Core.POJO_Entities.Hackathon;

@Repository
public interface IHackathonRepository extends JpaRepository<Hackathon, String> {
}
