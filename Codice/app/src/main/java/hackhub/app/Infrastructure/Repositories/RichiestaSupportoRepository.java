package hackhub.app.Infrastructure.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import hackhub.app.Application.IRepositories.IRichiestaSupportoRepository;
import hackhub.app.Core.POJO_Entities.RichiestaSupporto;

@Repository
public interface RichiestaSupportoRepository
        extends JpaRepository<RichiestaSupporto, String>, IRichiestaSupportoRepository {
}
