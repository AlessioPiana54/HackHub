package hackhub.app.Application.IRepositories;

import hackhub.app.Core.Enums.StatoHackathon;
import hackhub.app.Core.POJO_Entities.Hackathon;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.query.Param;

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
   * Trova un hackathon tramite ID (String).
   *
   * @param id Identificativo univoco dell'hackathon come stringa.
   * @return Optional contenente l'hackathon se trovato, altrimenti vuoto.
   */
  Optional<Hackathon> findByIdString(@Param("id") String id);

  /**
   * Trova un hackathon tramite ID (String) - metodo per compatibilità con JpaRepository.
   *
   * @param id Identificativo univoco dell'hackathon come stringa.
   * @return Optional contenente l'hackathon se trovato, altrimenti vuoto.
   */
  Optional<Hackathon> findById(@Param("id") String id);

  /**
   * Recupera tutti gli hackathon presenti nel sistema.
   *
   * @return Lista di tutti gli Hackathon.
   */
  List<Hackathon> findAll();

  /**
   * Trova gli hackathon assegnati a un giudice specifico.
   *
   * @param giudiceId L'ID del giudice.
   * @return Lista di Hackathon assegnati.
   */
  List<Hackathon> findByGiudiceId(String giudiceId);
  /**
   * Trova gli hackathon assegnati a un mentore specifico.
   *
   * @param mentoreId L'ID del mentore.
   * @return Lista di Hackathon assegnati.
   */
  List<Hackathon> findByMentoriId(String mentoreId);

  /**
   * Trova un hackathon tramite nome.
   *
   * @param nome Nome dell'hackathon.
   * @return Optional contenente l'hackathon se trovato, altrimenti vuoto.
   */
  Optional<Hackathon> findByNome(String nome);
}
