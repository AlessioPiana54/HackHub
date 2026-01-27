package hackhub.app.Application.IRepositories;

import java.util.Optional;
import hackhub.app.Core.POJO_Entities.Invito;
import hackhub.app.Core.POJO_Entities.User;
import hackhub.app.Core.POJO_Entities.Team;
import java.util.List;

public interface IInvitoRepository {
    List<Invito> findByDestinatario(User destinatario);

    List<Invito> findByTeam(Team team);

    Invito save(Invito invito);

    Optional<Invito> findById(String id);

    void delete(Invito invito);
}
