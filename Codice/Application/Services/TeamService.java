package Application.Services;

import Application.IRepositories.ITeamRepository;
import Application.IRepositories.IUserRepository;
import Application.Requests.CreaTeamRequest;
import Application.Requests.IscriviTeamRequest;
import Core.Enums.Ruolo;
import Core.Enums.StatoHackathon;
import Core.POJO_Entities.Hackathon;
import Application.IRepositories.IHackathonRepository;
import Application.IRepositories.IPartecipazioneRepository;
import Core.POJO_Entities.Partecipazione;
import Core.POJO_Entities.Team;
import java.util.List;
import Core.POJO_Entities.User;

public class TeamService {
    private final ITeamRepository teamRepository;
    private final IUserRepository userRepository;
    private final IHackathonRepository hackathonRepository;
    private final IPartecipazioneRepository partecipazioneRepository;

    public TeamService(ITeamRepository teamRepository, IUserRepository userRepo, IHackathonRepository hackathonRepo,
            IPartecipazioneRepository partecipazioneRepo) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepo;
        this.hackathonRepository = hackathonRepo;
        this.partecipazioneRepository = partecipazioneRepo;
    }

    public Team creaTeam(CreaTeamRequest request) {
        // NOTA: I controlli formali (date, stringhe vuote) sono già stati fatti dal
        // Validator.

        // 1. Recupero Utente (Logica che richiede il DB, quindi rimane nel Service)
        User new_leader = userRepository.findById(request.getLeaderSquadra());

        // 2. Validazione Esistenza (Domain Logic)
        if (new_leader == null) {
            throw new IllegalArgumentException("Utente non trovato nel database.");
        }

        // 2a. Validazione Unicità Nome Team
        if (teamRepository.existsByName(request.getNomeTeam())) {
            throw new IllegalArgumentException("Esiste già un Team con questo nome.");
        }

        // 3. Validazione Ruoli (Business Logic pura)
        if (!new_leader.haRuolo(Ruolo.UTENTE_SENZA_TEAM)) {
            throw new SecurityException("L'utente specificato non ha i permessi necessari.");
        }

        // 3b. Aggiornamento Ruolo Utente (Promozione a LEADER_TEAM)
        // Usiamo il costruttore di ricostruzione per "aggiornare" l'immutabile
        User leaderAggiornato = new User(
                new_leader.getId(),
                new_leader.getNome(),
                new_leader.getCognome(),
                new_leader.getEmail(),
                Ruolo.LEADER_TEAM);
        userRepository.edit(leaderAggiornato);

        // 4. Creazione Entità
        // Mappiamo la Request nell'Entità di dominio
        Team nuovoTeam = new Team(
                request.getNomeTeam(),
                leaderAggiornato);

        // 5. Persistenza
        teamRepository.save(nuovoTeam);

        return nuovoTeam;
    }

    public Partecipazione iscriviTeam(IscriviTeamRequest request) {
        Team team = teamRepository.findById(request.getIdTeam());
        if (team == null)
            throw new IllegalArgumentException("Team non trovato");

        // Regola: Solo il LEADER DEL TEAM può iscrivere il team
        if (team.getLeaderSquadra() == null || !team.getLeaderSquadra().getId().equals(request.getIdRichiedente())) {
            throw new SecurityException("Solo il Leader del Team può effettuare l'iscrizione.");
        }

        Hackathon hackathon = hackathonRepository.findById(request.getIdHackathon());
        if (hackathon == null)
            throw new IllegalArgumentException("Hackathon non trovato");

        // Regola: Hackathon deve essere in fase di iscrizione
        if (hackathon.getStato() != StatoHackathon.IN_ISCRIZIONE) {
            throw new IllegalArgumentException("Le iscrizioni per questo Hackathon non sono aperte.");
        }

        // Regola: Team iscritto ad un solo Hackathon per volta (non Concluso)
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
