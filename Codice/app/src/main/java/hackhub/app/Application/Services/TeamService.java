package hackhub.app.Application.Services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import hackhub.app.Application.IUnitOfWork.IUnitOfWork;
import hackhub.app.Application.Requests.CreaTeamRequest;
import hackhub.app.Application.Requests.IscriviTeamRequest;
import hackhub.app.Core.Enums.Ruolo;
import hackhub.app.Core.Enums.StatoHackathon;
import hackhub.app.Core.POJO_Entities.Hackathon;
import hackhub.app.Core.POJO_Entities.Partecipazione;
import hackhub.app.Core.POJO_Entities.Team;
import java.util.List;
import hackhub.app.Core.POJO_Entities.User;

@Service
@Transactional
public class TeamService {
    private final IUnitOfWork unitOfWork;

    @Autowired
    public TeamService(IUnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    public Team creaTeam(CreaTeamRequest request, String leaderId) {
        User new_leader = unitOfWork.userRepository().findById(leaderId)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato nel database."));

        if (unitOfWork.teamRepository().existsByNomeTeam(request.getNomeTeam())) {
            throw new IllegalArgumentException("Esiste già un Team con questo nome.");
        }

        if (new_leader.getRuolo() != Ruolo.UTENTE_SENZA_TEAM) {
            throw new SecurityException("L'utente specificato non ha i permessi necessari.");
        }

        User updatedLeader = new User(new_leader.getId(), new_leader.getNome(), new_leader.getCognome(),
                new_leader.getEmail(), new_leader.getPassword(), Ruolo.LEADER_TEAM);
        unitOfWork.userRepository().save(updatedLeader);

        Team nuovoTeam = new Team(request.getNomeTeam(), updatedLeader);
        unitOfWork.teamRepository().save(nuovoTeam);

        return nuovoTeam;
    }

    public Partecipazione iscriviTeam(IscriviTeamRequest request, String richiedenteId) {
        Team team = unitOfWork.teamRepository().findById(request.getIdTeam())
                .orElseThrow(() -> new IllegalArgumentException("Team non trovato"));

        if (team.getLeaderSquadra() == null || !team.getLeaderSquadra().getId().equals(richiedenteId)) {
            throw new SecurityException("Solo il Leader del Team può effettuare l'iscrizione.");
        }

        Hackathon hackathon = unitOfWork.hackathonRepository().findById(request.getIdHackathon())
                .orElseThrow(() -> new IllegalArgumentException("Hackathon non trovato"));

        if (hackathon.getStato() != StatoHackathon.IN_ISCRIZIONE) {
            throw new IllegalArgumentException("Le iscrizioni per questo Hackathon non sono aperte.");
        }

        List<Partecipazione> partecipazioni = unitOfWork.partecipazioneRepository().findByTeamId(request.getIdTeam());
        for (Partecipazione p : partecipazioni) {
            if (p.getHackathon().getStato() != StatoHackathon.CONCLUSO) {
                throw new IllegalArgumentException("Il Team è già iscritto ad un Hackathon attivo.");
            }
        }

        Partecipazione p = new Partecipazione(team, hackathon);
        unitOfWork.partecipazioneRepository().save(p);
        return p;
    }
}
