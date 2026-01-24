package hackhub.app.Application.Services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import hackhub.app.Application.IRepositories.ITeamRepository;
import hackhub.app.Application.IRepositories.IUserRepository;
import hackhub.app.Application.Requests.CreaTeamRequest;
import hackhub.app.Application.Requests.IscriviTeamRequest;
import hackhub.app.Core.Enums.Ruolo;
import hackhub.app.Core.Enums.StatoHackathon;
import hackhub.app.Core.POJO_Entities.Hackathon;
import hackhub.app.Application.IRepositories.IHackathonRepository;
import hackhub.app.Application.IRepositories.IPartecipazioneRepository;
import hackhub.app.Core.POJO_Entities.Partecipazione;
import hackhub.app.Core.POJO_Entities.Team;
import java.util.List;
import hackhub.app.Core.POJO_Entities.User;

@Service
@Transactional
public class TeamService {
    private final ITeamRepository teamRepository;
    private final IUserRepository userRepository;
    private final IHackathonRepository hackathonRepository;
    private final IPartecipazioneRepository partecipazioneRepository;

    @Autowired
    public TeamService(ITeamRepository teamRepository, IUserRepository userRepo, IHackathonRepository hackathonRepo,
            IPartecipazioneRepository partecipazioneRepo) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepo;
        this.hackathonRepository = hackathonRepo;
        this.partecipazioneRepository = partecipazioneRepo;
    }

    public Team creaTeam(CreaTeamRequest request) {
        User new_leader = userRepository.findById(request.getLeaderSquadra())
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato nel database."));

        if (teamRepository.existsByNomeTeam(request.getNomeTeam())) {
            throw new IllegalArgumentException("Esiste già un Team con questo nome.");
        }

        if (new_leader.getRuolo() != Ruolo.UTENTE_SENZA_TEAM) {
            throw new SecurityException("L'utente specificato non ha i permessi necessari.");
        }

        new_leader.setRuolo(Ruolo.LEADER_TEAM);
        userRepository.save(new_leader);

        Team nuovoTeam = new Team(request.getNomeTeam(), new_leader);
        teamRepository.save(nuovoTeam);

        return nuovoTeam;
    }

    public Partecipazione iscriviTeam(IscriviTeamRequest request) {
        Team team = teamRepository.findById(request.getIdTeam())
                .orElseThrow(() -> new IllegalArgumentException("Team non trovato"));

        if (team.getLeaderSquadra() == null || !team.getLeaderSquadra().getId().equals(request.getIdRichiedente())) {
            throw new SecurityException("Solo il Leader del Team può effettuare l'iscrizione.");
        }

        Hackathon hackathon = hackathonRepository.findById(request.getIdHackathon())
                .orElseThrow(() -> new IllegalArgumentException("Hackathon non trovato"));

        if (hackathon.getStato() != StatoHackathon.IN_ISCRIZIONE) {
            throw new IllegalArgumentException("Le iscrizioni per questo Hackathon non sono aperte.");
        }

        List<Partecipazione> partecipazioni = partecipazioneRepository.findByTeamId(request.getIdTeam());
        for (Partecipazione p : partecipazioni) {
            if (p.getHackathon().getStato() != StatoHackathon.CONCLUSO) {
                throw new IllegalArgumentException("Il Team è già iscritto ad un Hackathon attivo.");
            }
        }

        Partecipazione p = new Partecipazione(team, hackathon);
        partecipazioneRepository.save(p);
        return p;
    }
}
