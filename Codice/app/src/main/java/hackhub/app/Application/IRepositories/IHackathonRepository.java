package hackhub.app.Application.IRepositories;

import java.util.Optional;
import hackhub.app.Core.POJO_Entities.Hackathon;
import hackhub.app.Core.Enums.StatoHackathon;
import java.util.Collection;
import java.util.List;

public interface IHackathonRepository {
    List<Hackathon> findByStatoNotIn(Collection<StatoHackathon> stati);

    Hackathon save(Hackathon hackathon);

    Optional<Hackathon> findById(String id);

    List<Hackathon> findAll();
}
