package hackhub.app.Application.Services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import hackhub.app.Application.IUnitOfWork.IUnitOfWork;
import hackhub.app.Application.Requests.CreaTeamRequest;
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

        new_leader.setRuolo(Ruolo.LEADER_TEAM);
        unitOfWork.userRepository().save(new_leader);

        Team nuovoTeam = new Team(request.getNomeTeam(), new_leader);
        unitOfWork.teamRepository().save(nuovoTeam);

        return nuovoTeam;
    }

    public Partecipazione iscriviTeam(String teamId, String hackathonId, String richiedenteId) {
        Team team = unitOfWork.teamRepository().findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team non trovato"));

        if (team.getLeaderSquadra() == null || !team.getLeaderSquadra().getId().equals(richiedenteId)) {
            throw new SecurityException("Solo il Leader del Team può effettuare l'iscrizione.");
        }

        Hackathon hackathon = unitOfWork.hackathonRepository().findById(hackathonId)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon non trovato"));

        if (hackathon.getStato() != StatoHackathon.IN_ISCRIZIONE) {
            throw new IllegalArgumentException("Le iscrizioni per questo Hackathon non sono aperte.");
        }

        List<Partecipazione> partecipazioni = unitOfWork.partecipazioneRepository().findByTeamId(teamId);
        for (Partecipazione p : partecipazioni) {
            if (p.getHackathon().getStato() != StatoHackathon.CONCLUSO) {
                throw new IllegalArgumentException("Il Team è già iscritto ad un Hackathon attivo.");
            }
        }

        Partecipazione p = new Partecipazione(team, hackathon);
        unitOfWork.partecipazioneRepository().save(p);
        return p;
    }

    public void abbandonaTeam(String teamId, String memberId) {
        Team team = unitOfWork.teamRepository().findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team non trovato"));

        User member = unitOfWork.userRepository().findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        // Check if the user is part of the team
        boolean isMember = team.getMembri().stream()
                .anyMatch(m -> m.getId().equals(memberId));

        if (!isMember) {
            throw new IllegalArgumentException("L'utente non fa parte di questo Team.");
        }

        // Check if the user is the leader
        if (team.getLeaderSquadra().getId().equals(memberId)) {
            throw new IllegalArgumentException("Il Leader deve cedere il ruolo prima di abbandonare il team.");
        }

        // Remove member from team
        team.getMembri().removeIf(m -> m.getId().equals(memberId));
        unitOfWork.teamRepository().save(team);

        // Update user role
        // Update user role
        member.setRuolo(Ruolo.UTENTE_SENZA_TEAM);
        unitOfWork.userRepository().save(member);
    }
}
