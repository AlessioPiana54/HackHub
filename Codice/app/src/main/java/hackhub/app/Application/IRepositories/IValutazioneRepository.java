package hackhub.app.Application.IRepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import hackhub.app.Core.POJO_Entities.Valutazione;

@Repository
public interface IValutazioneRepository extends JpaRepository<Valutazione, String> {
    boolean existsBySottomissioneId(String sottomissioneId);
}
