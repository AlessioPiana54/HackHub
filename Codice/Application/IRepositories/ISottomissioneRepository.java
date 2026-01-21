package Application.IRepositories;

import Core.POJO_Entities.Sottomissione;
import java.util.List;

public interface ISottomissioneRepository {
    void save(Sottomissione sottomissione);

    void edit(Sottomissione sottomissione);

    Sottomissione findById(String id);

    List<Sottomissione> findAll();

    List<Sottomissione> findByHackathonId(String idHackathon);

    List<Sottomissione> findByTeamId(String idTeam);

    void deleteById(String id);
}
