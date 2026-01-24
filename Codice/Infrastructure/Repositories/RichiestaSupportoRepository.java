package Infrastructure.Repositories;

import Application.IRepositories.IRichiestaSupportoRepository;
import Core.POJO_Entities.RichiestaSupporto;
import Infrastructure.Databases.InMemoryDatabase;

public class RichiestaSupportoRepository implements IRichiestaSupportoRepository {
    private final InMemoryDatabase db;

    public RichiestaSupportoRepository(InMemoryDatabase db) {
        this.db = db;
    }

    @Override
    public void save(RichiestaSupporto richiesta) {
        db.saveRichiestaSupporto(richiesta);
    }

    @Override
    public RichiestaSupporto findById(String id) {
        return db.getRichiestaSupporto(id);
    }
}
