package hackhub.app.Application.IRepositories;

import hackhub.app.Core.POJO_Entities.Sottomissione;
import java.util.List;

public interface ISottomissioneRepository {
    /**
     * Trova tutte le sottomissioni relative a un hackathon specifico.
     * 
     * @param hackathonId ID dell'hackathon.
     * @return Lista di sottomissioni.
     */
    List<Sottomissione> findByPartecipazioneHackathonId(String hackathonId);

    /**
     * Trova tutte le sottomissioni effettuate da un team specifico.
     * 
     * @param teamId ID del team.
     * @return Lista di sottomissioni del team.
     */
    List<Sottomissione> findByPartecipazioneTeamId(String teamId);

    /**
     * Salva o aggiorna una sottomissione.
     * 
     * @param sottomissione L'entità Sottomissione da salvare.
     * @return L'entità salvata.
     */
    Sottomissione save(Sottomissione sottomissione);

    /**
     * Trova una sottomissione tramite ID.
     * 
     * @param id Identificativo univoco della sottomissione.
     * @return Optional contenente la sottomissione se trovata.
     */
    java.util.Optional<Sottomissione> findById(String id);

    /**
     * Verifica se esiste una sottomissione per una data partecipazione.
     * 
     * @param partecipazioneId ID della partecipazione.
     * @return true se esiste una sottomissione, false altrimenti.
     */
    boolean existsByPartecipazioneId(String partecipazioneId);

    /**
     * Verifica se un team specifico ha inviato sottomissioni per un hackathon.
     * Ottimizzato per evitare il caricamento di entità non necessarie.
     * 
     * @param hackathonId ID dell'hackathon.
     * @param teamId      ID del team.
     * @return true se il team ha inviato una sottomissione per l'hackathon.
     */
    boolean existsByPartecipazione_Hackathon_IdAndPartecipazione_Team_Id(String hackathonId, String teamId);
}
