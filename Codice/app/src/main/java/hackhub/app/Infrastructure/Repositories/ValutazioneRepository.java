package hackhub.app.Infrastructure.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import hackhub.app.Application.IRepositories.IValutazioneRepository;
import hackhub.app.Core.POJO_Entities.Valutazione;

@Repository
public interface ValutazioneRepository extends JpaRepository<Valutazione, String>, IValutazioneRepository {
}
