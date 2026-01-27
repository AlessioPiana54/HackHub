package hackhub.app.Application.IRepositories;

import hackhub.app.Core.POJO_Entities.Valutazione;

public interface IValutazioneRepository {
    boolean existsBySottomissioneId(String sottomissioneId);

    Valutazione save(Valutazione valutazione);

    java.util.Optional<Valutazione> findById(String id);
}
