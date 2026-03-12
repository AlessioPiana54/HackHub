package hackhub.app.Application.Services;

import hackhub.app.Application.IUnitOfWork.IUnitOfWork;
import hackhub.app.Application.Requests.CreaInvitoRequest;
import hackhub.app.Application.Requests.RispostaInvitoRequest;
import hackhub.app.Core.Enums.Ruolo;
import hackhub.app.Core.Enums.StatoHackathon;
import hackhub.app.Core.POJO_Entities.Hackathon;
import hackhub.app.Core.POJO_Entities.Invito;
import hackhub.app.Core.POJO_Entities.Partecipazione;
import hackhub.app.Core.POJO_Entities.Team;
import hackhub.app.Core.POJO_Entities.User;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servizio per la gestione degli inviti ai team.
 */
@Service
@Transactional
public class InvitoService extends AbstractService {

  public InvitoService(IUnitOfWork unitOfWork) {
    super(unitOfWork);
  }

  /**
   * Invia un invito a un utente per unirsi a un team.
   *
   * @param request    i dati dell'invito (email destinatario, team)
   * @param mittenteId l'ID dell'utente che invia l'invito
   * @return l'Invito creato
   * @throws IllegalArgumentException se team, mittente o destinatario non
   *                                  trovati, o se il destinatario ha già un
   *                                  ruolo
   */
  public Invito inviaInvito(CreaInvitoRequest request, String mittenteId) {
    Team team = findTeamOrThrow(request.getTeamId());
    User mittente = findUserOrThrow(mittenteId);

    User destinatario = unitOfWork
      .userRepository()
      .findByEmail(request.getEmailDestinatario());
    if (destinatario == null) throw new IllegalArgumentException(
      "Destinatario non trovato"
    );

    validateUserInTeam(
      team,
      mittenteId,
      "Solo i membri del team o il leader possono inviare inviti."
    );

    validateUserRole(
      destinatario,
      Ruolo.UTENTE_SENZA_TEAM,
      "L'utente ha già un team o un ruolo incompatibile."
    );

    Invito invito = new Invito(team, destinatario, mittente);
    unitOfWork.invitoRepository().save(invito);
    return invito;
  }

  /**
   * Gestisce la risposta a un invito (accettazione o rifiuto).
   *
   * @param request i dati della risposta (ID invito, accettato/rifiutato)
   * @param userId  l'ID dell'utente che risponde
   * @throws IllegalArgumentException se invito o utente non trovati, o l'invito
   *                                  non è per l'utente
   */
  public void gestisciRisposta(
    RispostaInvitoRequest request,
    String userId,
    String invitoId
  ) {
    Invito invito = unitOfWork
      .invitoRepository()
      .findById(invitoId)
      .orElseThrow(() -> new IllegalArgumentException("Invito non trovato"));

    User user = findUserOrThrow(userId);

    if (!invito.getDestinatario().getId().equals(user.getId())) {
      throw new IllegalArgumentException("Questo invito non è per te.");
    }

    if (request.isAccettato()) {
      accettaInvito(invito, user);
    } else {
      rifiutaInvito(invito);
    }
  }

  private void accettaInvito(Invito invito, User user) {
    Team team = invito.getTeam();

    List<Partecipazione> partecipazioni = unitOfWork
      .partecipazioneRepository()
      .findByTeamId(team.getId());
    for (Partecipazione p : partecipazioni) {
      Hackathon h = p.getHackathon();
      if (
        h.getStato() == StatoHackathon.IN_CORSO ||
        h.getStato() == StatoHackathon.IN_VALUTAZIONE ||
        h.getStato() == StatoHackathon.IN_PREMIAZIONE
      ) {
        throw new IllegalStateException(
          "Impossibile entrare nel team: il team è partecipante ad un Hackathon attivo."
        );
      }
    }

    user.setRuolo(Ruolo.MEMBRO_TEAM);
    team.getMembri().add(user);
    unitOfWork.userRepository().save(user);
    unitOfWork.teamRepository().save(team);
    unitOfWork.invitoRepository().delete(invito);
  }

  private void rifiutaInvito(Invito invito) {
    unitOfWork.invitoRepository().delete(invito);
  }
}
