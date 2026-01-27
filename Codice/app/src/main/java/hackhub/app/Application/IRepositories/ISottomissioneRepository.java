package hackhub.app.Application.IRepositories;

import hackhub.app.Core.POJO_Entities.Sottomissione;
import java.util.List;

public interface ISottomissioneRepository {
    List<Sottomissione> findByPartecipazioneHackathonId(String hackathonId);

    List<Sottomissione> findByPartecipazioneTeamId(String teamId);

    Sottomissione save(Sottomissione sottomissione);

    java.util.Optional<Sottomissione> findById(String id);
}
