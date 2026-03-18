package hackhub.app.Application.Services;

import static java.util.stream.Collectors.toList;

import hackhub.app.Application.DTOs.SegnalazioneDTO;
import hackhub.app.Application.IUnitOfWork.IUnitOfWork;
import hackhub.app.Application.Requests.CreaSegnalazioneRequest;
import hackhub.app.Core.POJO_Entities.Hackathon;
import hackhub.app.Core.POJO_Entities.Partecipazione;
import hackhub.app.Core.POJO_Entities.Segnalazione;
import hackhub.app.Core.POJO_Entities.User;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servizio per la gestione delle segnalazioni da parte dei mentori.
 */
@Service
@Transactional
public class SegnalazioneService extends AbstractService {

  public SegnalazioneService(
    IUnitOfWork unitOfWork,
    EntityFinder entityFinder,
    AuthorizationChecker authorizationChecker
  ) {
    super(unitOfWork, entityFinder, authorizationChecker);
  }

  /**
   * Crea una nuova segnalazione per un team da parte di un mentore.
   *
   * @param request   i dati della segnalazione
   * @param mentoreId l'ID del mentore che crea la segnalazione
   * @return la Segnalazione creata
   * @throws IllegalArgumentException se mentore, team o hackathon non trovati, o
   *                                  il team non partecipa
   * @throws SecurityException        se l'utente non è un mentore per l'hackathon
   */
  public Segnalazione creaSegnalazione(
    CreaSegnalazioneRequest request,
    String mentoreId
  ) {
    User mentore = findUserOrThrow(mentoreId);
    Partecipazione partecipazione = findPartecipazioneOrThrow(
      request.getIdTeam(),
      request.getIdHackathon()
    );

    Hackathon hackathon = partecipazione.getHackathon();
    validateUserIsMentorInHackathon(
      hackathon,
      mentoreId,
      "L'utente " +
      mentore.getNome() +
      " non è un mentore per questo Hackathon."
    );

    Segnalazione segnalazione = new Segnalazione(
      partecipazione,
      mentore,
      request.getDescrizione()
    );
    unitOfWork.segnalazioneRepository().save(segnalazione);
    return segnalazione;
  }

  /**
   * Restituisce la lista delle segnalazioni per un Hackathon (chiamata
   * dall'organizzatore).
   *
   * @param idHackathon l'ID dell'Hackathon
   * @param idOrganizer l'ID dell'organizzatore richiedente
   * @return la lista di SegnalazioneDTO
   * @throws IllegalArgumentException se l'hackathon non viene trovato
   * @throws SecurityException        se il richiedente non è l'organizzatore
   */
  public List<SegnalazioneDTO> getSegnalazioni(
    String idHackathon,
    String idUser
  ) {
    Hackathon h = findHackathonOrThrow(idHackathon);

    boolean isOrganizer = h.getOrganizzatore().getId().equals(idUser);
    boolean isMentor = h
      .getMentori()
      .stream()
      .anyMatch(m -> m.getId().equals(idUser));

    if (isOrganizer || isMentor) {
      // Organizzatore e Mentori vedono tutto
      return unitOfWork
        .segnalazioneRepository()
        .findByPartecipazioneHackathonId(idHackathon)
        .stream()
        .map(this::mapToDTO)
        .collect(toList());
    }

    // Per i partecipanti, filtriamo solo le segnalazioni del proprio team
    List<hackhub.app.Core.POJO_Entities.Team> userTeams = unitOfWork
      .teamRepository()
      .findByMembriId(idUser);

    return unitOfWork
      .segnalazioneRepository()
      .findByPartecipazioneHackathonId(idHackathon)
      .stream()
      .filter(s ->
        userTeams.stream().anyMatch(t -> t.getId().equals(s.getTeam().getId()))
      )
      .map(this::mapToDTO)
      .collect(toList());
  }

  // --- Private Helper Methods ---

  private SegnalazioneDTO mapToDTO(Segnalazione s) {
    return new SegnalazioneDTO(
      s.getId(),
      s.getHackathon().getId(),
      s.getHackathon().getNome(),
      s.getTeam().getId(),
      s.getTeam().getNomeTeam(),
      s.getMentore().getId(),
      s.getMentore().getNome(),
      s.getDescrizione(),
      s.getDataSegnalazione()
    );
  }
}
