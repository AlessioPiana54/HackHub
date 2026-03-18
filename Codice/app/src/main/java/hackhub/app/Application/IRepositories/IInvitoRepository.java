package hackhub.app.Application.IRepositories;

import java.util.Optional;
import hackhub.app.Core.POJO_Entities.Invito;
import hackhub.app.Core.POJO_Entities.User;
import hackhub.app.Core.POJO_Entities.Team;
import java.util.List;

public interface IInvitoRepository {
    /**
     * Trova tutti gli inviti ricevuti da un utente specifico.
     * 
     * @param destinatario L'utente destinatario degli inviti.
     * @return Lista di inviti per l'utente.
     */
    List<Invito> findByDestinatario(User destinatario);

    /**
     * Trova tutti gli inviti inviati per conto di un team specifico.
     * 
     * @param team Il team che ha inviato gli inviti.
     * @return Lista di inviti associati al team.
     */
    List<Invito> findByTeam(Team team);

    /**
     * Salva o aggiorna un invito.
     * 
     * @param invito L'entità Invito da salvare.
     * @return L'entità salvata.
     */
    Invito save(Invito invito);

    /**
     * Trova un invito tramite ID.
     * 
     * @param id Identificativo univoco dell'invito.
     * @return Optional contenente l'invito se trovato.
     */
    Optional<Invito> findById(String id);

    /**
     * Elimina un invito dal sistema.
     * 
     * @param invito L'entità Invito da eliminare.
     */
    void delete(Invito invito);

    /**
     * Recupera tutti gli inviti nel sistema.
     *
     * @return Lista di tutti gli inviti.
     */
    List<Invito> findAll();

    /**
     * Trova tutti gli inviti inviati da un utente specifico.
     *
     * @param mittente L'utente mittente degli inviti.
     * @return Lista di inviti inviati dall'utente.
     */
    List<Invito> findByMittente(User mittente);
}
