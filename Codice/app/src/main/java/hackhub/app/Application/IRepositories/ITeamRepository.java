package hackhub.app.Application.IRepositories;

import hackhub.app.Core.POJO_Entities.Team;
import java.util.Optional;

public interface ITeamRepository {
    /**
     * Verifica se esiste già un team con il nome specificato.
     * 
     * @param nomeTeam Il nome del team da verificare.
     * @return true se esiste un team con quel nome, false altrimenti.
     */
    boolean existsByNomeTeam(String nomeTeam);

    /**
     * Salva o aggiorna un team.
     * 
     * @param team L'entità Team da salvare.
     * @return L'entità salvata.
     */
    Team save(Team team);

    /**
     * Trova un team tramite ID.
     * 
     * @param id Identificativo univoco del team.
     * @return Optional contenente il team se trovato.
     */
    Optional<Team> findById(String id);
}
