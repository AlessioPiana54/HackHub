package Application.IRepositories;

import java.util.List;

import Core.POJO_Entities.Hackathon;

public interface IHackathonRepository {
    void save(Hackathon hackathon);
    void edit(Hackathon hackathon);
    Hackathon findById(String id);
    List<Hackathon> findAll();
    void deleteById(String id);
}
