package hackhub.app.Infrastructure.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import hackhub.app.Application.IRepositories.IPartecipazioneRepository;
import hackhub.app.Core.POJO_Entities.Partecipazione;

@Repository
public interface PartecipazioneRepository extends JpaRepository<Partecipazione, String>, IPartecipazioneRepository {
}
