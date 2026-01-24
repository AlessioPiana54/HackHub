package hackhub.app.Application.IRepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import hackhub.app.Core.POJO_Entities.Partecipazione;
import java.util.List;
import java.util.Optional;

@Repository
public interface IPartecipazioneRepository extends JpaRepository<Partecipazione, String> {
    List<Partecipazione> findByTeamId(String teamId);

    List<Partecipazione> findByHackathonId(String hackathonId);

    Optional<Partecipazione> findByTeamIdAndHackathonId(String teamId, String hackathonId);
}
