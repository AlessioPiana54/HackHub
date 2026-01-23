package Application.IRepositories;

import Core.POJO_Entities.Invito;
import Core.POJO_Entities.Team;
import Core.POJO_Entities.User;
import java.util.List;

public interface IInvitoRepository {
    void save(Invito invito);

    void delete(Invito invito);

    Invito findById(String id);

    List<Invito> findByDestinatario(User destinatario);
    
    List<Invito> findByTeam(Team team);
}
