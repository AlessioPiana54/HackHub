package hackhub.app.Application.IRepositories;

import hackhub.app.Core.POJO_Entities.Partecipazione;
import java.util.List;
import java.util.Optional;

public interface IPartecipazioneRepository {
    /**
     * Trova tutte le partecipazioni di un team specifico.
     * 
     * @param teamId ID del team.
     * @return Lista di partecipazioni del team.
     */
    List<Partecipazione> findByTeamId(String teamId);

    /**
     * Trova tutte le partecipazioni a un hackathon specifico.
     * 
     * @param hackathonId ID dell'hackathon.
     * @return Lista di partecipazioni per l'hackathon.
     */
    List<Partecipazione> findByHackathonId(String hackathonId);

    /**
     * Cerca una partecipazione specifica di un team a un hackathon.
     * 
     * @param teamId      ID del team.
     * @param hackathonId ID dell'hackathon.
     * @return Optional contenente la partecipazione se esiste.
     */
    Optional<Partecipazione> findByTeamIdAndHackathonId(String teamId, String hackathonId);

    /**
     * Salva o aggiorna una partecipazione.
     * 
     * @param partecipazione L'entità Partecipazione da salvare.
     * @return L'entità salvata.
     */
    Partecipazione save(Partecipazione partecipazione);

    /**
     * Trova una partecipazione tramite ID.
     * 
     * @param id Identificativo univoco della partecipazione.
     * @return Optional contenente la partecipazione se trovata.
     */
    Optional<Partecipazione> findById(String id);
}
