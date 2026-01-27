package hackhub.app.Infrastructure.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import hackhub.app.Application.IRepositories.IInvitoRepository;
import hackhub.app.Core.POJO_Entities.Invito;

@Repository
public interface InvitoRepository extends JpaRepository<Invito, String>, IInvitoRepository {
}
