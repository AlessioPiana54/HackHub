package hackhub.app.Application.IRepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import hackhub.app.Core.POJO_Entities.Invito;
import hackhub.app.Core.POJO_Entities.User;
import hackhub.app.Core.POJO_Entities.Team;
import java.util.List;

@Repository
public interface IInvitoRepository extends JpaRepository<Invito, String> {
    List<Invito> findByDestinatario(User destinatario);

    List<Invito> findByTeam(Team team);
}
