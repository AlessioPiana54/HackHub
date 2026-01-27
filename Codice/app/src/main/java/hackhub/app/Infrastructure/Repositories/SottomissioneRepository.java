package hackhub.app.Infrastructure.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import hackhub.app.Application.IRepositories.ISottomissioneRepository;
import hackhub.app.Core.POJO_Entities.Sottomissione;

@Repository
public interface SottomissioneRepository extends JpaRepository<Sottomissione, String>, ISottomissioneRepository {
}
