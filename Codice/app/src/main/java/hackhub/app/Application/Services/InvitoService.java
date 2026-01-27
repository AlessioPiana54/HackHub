package hackhub.app.Application.Services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
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

@Service
@Transactional
public class InvitoService {
    private final IUnitOfWork unitOfWork;

    @Autowired
    public InvitoService(IUnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    public Invito inviaInvito(CreaInvitoRequest request) {
        Team team = unitOfWork.teamRepository().findById(request.getTeamId())
                .orElseThrow(() -> new IllegalArgumentException("Team non trovato"));
        User mittente = unitOfWork.userRepository().findById(request.getUserMittenteId())
                .orElseThrow(() -> new IllegalArgumentException("Mittente non trovato"));

        User destinatario = unitOfWork.userRepository().findByEmail(request.getEmailDestinatario());
        if (destinatario == null)
            throw new IllegalArgumentException("Destinatario non trovato");

        if (!team.getLeaderSquadra().getId().equals(mittente.getId()) &&
                team.getMembri().stream().noneMatch(m -> m.getId().equals(mittente.getId()))) {
            throw new IllegalArgumentException("Solo i membri del team o il leader possono inviare inviti.");
        }

        if (destinatario.getRuolo() != Ruolo.UTENTE_SENZA_TEAM) {
            throw new IllegalArgumentException("L'utente ha già un team o un ruolo incompatibile.");
        }

        Invito invito = new Invito(team, destinatario, mittente);

        // Aggiornamento lato inverso non necessario con JPA se salvo l'Invito nel DB.

        unitOfWork.invitoRepository().save(invito);

        // teamRepository.save(team);
        // Non necessario, basta salvare invito.

        return invito;
    }

    public void gestisciRisposta(RispostaInvitoRequest request) {
        Invito invito = unitOfWork.invitoRepository().findById(request.getInvitoId())
                .orElseThrow(() -> new IllegalArgumentException("Invito non trovato"));

        User user = unitOfWork.userRepository().findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

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

        List<Partecipazione> partecipazioni = unitOfWork.partecipazioneRepository().findByTeamId(team.getId());
        for (Partecipazione p : partecipazioni) {
            Hackathon h = p.getHackathon();
            if (h.getStato() == StatoHackathon.IN_CORSO ||
                    h.getStato() == StatoHackathon.IN_VALUTAZIONE ||
                    h.getStato() == StatoHackathon.IN_PREMIAZIONE) {
                throw new IllegalStateException(
                        "Impossibile entrare nel team: il team è partecipante ad un Hackathon attivo.");
            }
        }

        user.setRuolo(Ruolo.MEMBRO_TEAM);
        team.getMembri().add(user); // Necessario per aggiornare la relazione @ManyToMany

        // team.getInvitiInSospeso().remove(invito); viene fatto automaticamente

        unitOfWork.userRepository().save(user); // è in realtà un update
        unitOfWork.teamRepository().save(team); // è in realtà un update
        unitOfWork.invitoRepository().delete(invito);
    }

    private void rifiutaInvito(Invito invito) {
        unitOfWork.invitoRepository().delete(invito);
    }
}
