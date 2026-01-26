package hackhub.app.Application.IRepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import hackhub.app.Core.POJO_Entities.RichiestaSupporto;

@Repository
public interface IRichiestaSupportoRepository extends JpaRepository<RichiestaSupporto, String> {
    java.util.List<RichiestaSupporto> findByPartecipazioneHackathonId(String hackathonId);

    java.util.List<RichiestaSupporto> findByPartecipazioneHackathonIdAndPartecipazioneTeamId(String hackathonId,
            String teamId);
}
