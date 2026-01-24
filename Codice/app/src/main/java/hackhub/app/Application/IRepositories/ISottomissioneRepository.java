package hackhub.app.Application.IRepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import hackhub.app.Core.POJO_Entities.Sottomissione;
import java.util.List;

@Repository
public interface ISottomissioneRepository extends JpaRepository<Sottomissione, String> {
    List<Sottomissione> findByPartecipazioneHackathonId(String hackathonId);

    List<Sottomissione> findByPartecipazioneTeamId(String teamId);
}
