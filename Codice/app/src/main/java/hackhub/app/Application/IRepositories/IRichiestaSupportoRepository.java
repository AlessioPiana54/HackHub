package hackhub.app.Application.IRepositories;

import hackhub.app.Core.POJO_Entities.RichiestaSupporto;

public interface IRichiestaSupportoRepository {
    java.util.List<RichiestaSupporto> findByPartecipazioneHackathonId(String hackathonId);

    java.util.List<RichiestaSupporto> findByPartecipazioneHackathonIdAndPartecipazioneTeamId(String hackathonId,
            String teamId);

    RichiestaSupporto save(RichiestaSupporto richiestaSupporto);

    java.util.Optional<RichiestaSupporto> findById(String id);
}
