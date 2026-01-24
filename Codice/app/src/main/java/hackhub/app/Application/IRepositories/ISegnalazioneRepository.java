package hackhub.app.Application.IRepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import hackhub.app.Core.POJO_Entities.Segnalazione;
import java.util.List;

@Repository
public interface ISegnalazioneRepository extends JpaRepository<Segnalazione, String> {
    List<Segnalazione> findByHackathonId(String hackathonId);
}
