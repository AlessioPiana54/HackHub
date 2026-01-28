package hackhub.app.Application.IRepositories;

import java.util.List;
import java.util.Optional;

import hackhub.app.Core.POJO_Entities.Valutazione;

public interface IValutazioneRepository {
    /**
     * Verifica se esiste una valutazione per una specifica sottomissione.
     * 
     * @param sottomissioneId ID della sottomissione.
     * @return true se esiste una valutazione, false altrimenti.
     */
    boolean existsBySottomissioneId(String sottomissioneId);

    /**
     * Trova la valutazione associata a una sottomissione specifica.
     * 
     * @param sottomissioneId ID della sottomissione.
     * @return Optional contenente la valutazione se trovata.
     */
    Optional<Valutazione> findBySottomissioneId(String sottomissioneId);

    /**
     * Salva o aggiorna una valutazione.
     * 
     * @param valutazione L'entità Valutazione da salvare.
     * @return L'entità salvata.
     */
    Valutazione save(Valutazione valutazione);

    /**
     * Trova una valutazione tramite ID.
     * 
     * @param id Identificativo univoco della valutazione.
     * @return Optional contenente la valutazione se trovata.
     */
    Optional<Valutazione> findById(String id);

    /**
     * Recupera tutte le valutazioni relative alle sottomissioni di un hackathon
     * specifico.
     * Ottimizzato per il recupero batch delle valutazioni.
     * 
     * @param hackathonId ID dell'hackathon.
     * @return Lista di tutte le valutazioni dell'hackathon.
     */
    List<Valutazione> findAllBySottomissione_Partecipazione_Hackathon_Id(String hackathonId);
}
