package hackhub.app.Application.IRepositories;

import hackhub.app.Core.POJO_Entities.Segnalazione;
import java.util.List;

public interface ISegnalazioneRepository {
    /**
     * Trova tutte le segnalazioni relative a un hackathon specifico.
     * 
     * @param hackathonId ID dell'hackathon.
     * @return Lista di segnalazioni.
     */
    List<Segnalazione> findByPartecipazioneHackathonId(String hackathonId);

    /**
     * Salva o aggiorna una segnalazione.
     * 
     * @param segnalazione L'entità Segnalazione da salvare.
     * @return L'entità salvata.
     */
    Segnalazione save(Segnalazione segnalazione);

    /**
     * Trova una segnalazione tramite ID.
     * 
     * @param id Identificativo univoco della segnalazione.
     * @return Optional contenente la segnalazione se trovata.
     */
    java.util.Optional<Segnalazione> findById(String id);
}
