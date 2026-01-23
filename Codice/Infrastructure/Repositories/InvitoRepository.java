package Infrastructure.Repositories;

import Application.IRepositories.IInvitoRepository;
import Core.POJO_Entities.Invito;
import Core.POJO_Entities.Team;
import Core.POJO_Entities.User;
import Infrastructure.Databases.InMemoryDatabase;
import java.util.List;
import java.util.stream.Collectors;

public class InvitoRepository implements IInvitoRepository {
    private InMemoryDatabase db;

    public InvitoRepository(InMemoryDatabase db) {
        this.db = db;
    }

    @Override
    public void save(Invito invito) {
        db.saveInvito(invito);
    }

    @Override
    public void delete(Invito invito) {
        db.deleteInvito(invito.getId());
    }

    @Override
    public Invito findById(String id) {
        return db.getInvito(id);
    }

    @Override
    public List<Invito> findByDestinatario(User destinatario) {
        return db.getAllInviti().stream()
                .filter(i -> i.getDestinatario().getId().equals(destinatario.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Invito> findByTeam(Team team) {
        return db.getAllInviti().stream()
                .filter(i -> i.getTeam().getId().equals(team.getId()))
                .collect(Collectors.toList());
    }
}
