package hackhub.app.Infrastructure.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import hackhub.app.Application.IRepositories.ISegnalazioneRepository;
import hackhub.app.Core.POJO_Entities.Segnalazione;

@Repository
public interface SegnalazioneRepository extends JpaRepository<Segnalazione, String>, ISegnalazioneRepository {
}
