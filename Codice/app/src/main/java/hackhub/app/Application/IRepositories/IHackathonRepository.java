package hackhub.app.Application.IRepositories;

import java.util.Optional;
import hackhub.app.Core.POJO_Entities.Hackathon;
import hackhub.app.Core.Enums.StatoHackathon;
import java.util.Collection;
import java.util.List;

public interface IHackathonRepository {
    /**
     * Trova tutti gli hackathon il cui stato NON è tra quelli specificati.
     * Utile per filtrare hackathon attivi/non conclusi escludendo stati specifici.
     * 
     * @param stati Collezione di stati da escludere.
     * @return Lista di Hackathon che non si trovano negli stati forniti.
     */
    List<Hackathon> findByStatoNotIn(Collection<StatoHackathon> stati);

    /**
     * Salva o aggiorna un hackathon.
     * 
     * @param hackathon L'entità Hackathon da salvare.
     * @return L'entità salvata.
     */
    Hackathon save(Hackathon hackathon);

    /**
     * Trova un hackathon tramite ID.
     * 
     * @param id Identificativo univoco dell'hackathon.
     * @return Optional contenente l'hackathon se trovato, altrimenti vuoto.
     */
    Optional<Hackathon> findById(String id);

    /**
     * Recupera tutti gli hackathon presenti nel sistema.
     * 
     * @return Lista di tutti gli hackathon.
     */
    List<Hackathon> findAll();
}
