package Application.IRepositories;

import Core.POJO_Entities.RichiestaSupporto;

public interface IRichiestaSupportoRepository {
    void save(RichiestaSupporto richiesta);

    RichiestaSupporto findById(String id);

}
