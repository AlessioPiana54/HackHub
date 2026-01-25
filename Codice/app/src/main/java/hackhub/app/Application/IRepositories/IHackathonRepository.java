package hackhub.app.Application.IRepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import hackhub.app.Core.POJO_Entities.Hackathon;
import hackhub.app.Core.Enums.StatoHackathon;
import java.util.Collection;
import java.util.List;

@Repository
public interface IHackathonRepository extends JpaRepository<Hackathon, String> {
    List<Hackathon> findByStatoNotIn(Collection<StatoHackathon> stati);
}
