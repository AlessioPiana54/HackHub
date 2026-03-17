package hackhub.app.Application.IRepositories;

import hackhub.app.Core.POJO_Entities.Team;
import java.util.Optional;
import java.util.List;

public interface ITeamRepository {
  /**
   * Verifica se esiste già un team con il nome specificato.
   *
   * @param nomeTeam Il nome del team da verificare.
   * @return true se esiste un team con quel nome, false altrimenti.
   */
  boolean existsByNomeTeam(String nomeTeam);

  /**
   * Verifica se esiste un team con il leader specificato.
   *
   * @param leaderId L'ID del leader da verificare.
   * @return true se esiste un team con quel leader, false altrimenti.
   */
  boolean existsByLeaderId(String leaderId);

  /**
   * Elimina un team in base al leader ID.
   *
   * @param leaderId L'ID del leader del team da eliminare.
   */
  void deleteByLeaderId(String leaderId);

  /**
   * Salva o aggiorna un team.
   *
   * @param team L'entità Team da salvare.
   * @return L'entità salvata.
   */
  Team save(Team team);

  /**
   * Trova tutti i team di cui fa parte un determinato utente.
   *
   * @param userId L'ID dell'utente.
   * @return Lista di team in cui l'utente è membro.
   */
  java.util.List<Team> findByMembriId(String userId);

  /**
   * Trova un team tramite ID (String).
   *
   * @param id Identificativo univoco del team come stringa.
   * @return Optional contenente il team se trovato.
   */
  Optional<Team> findByIdString(String id);
}
