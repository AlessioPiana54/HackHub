package hackhub.app.Application.IRepositories;

import java.util.List;
import java.util.Optional;

import hackhub.app.Core.POJO_Entities.User;

public interface IUserRepository {
    /**
     * Trova un utente tramite il suo indirizzo email.
     * 
     * @param email L'indirizzo email dell'utente.
     * @return L'entità User associata all'email.
     */
    User findByEmail(String email);

    /**
     * Salva o aggiorna un utente.
     * 
     * @param user L'entità User da salvare.
     * @return L'entità salvata.
     */
    User save(User user);

    /**
     * Trova un utente tramite ID.
     * 
     * @param id Identificativo univoco dell'utente.
     * @return Optional contenente l'utente se trovato.
     */
    Optional<User> findById(String id);

    /**
     * Recupera una lista di utenti dato un elenco di ID.
     * 
     * @param ids Iterable contenente gli ID degli utenti da recuperare.
     * @return Lista degli utenti trovati.
     */
    List<User> findAllById(Iterable<String> ids);
}
