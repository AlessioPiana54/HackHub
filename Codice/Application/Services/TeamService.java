package Application.Services;

import Application.IRepositories.ITeamRepository;
import Application.IRepositories.IUserRepository;
import Application.Requests.CreaTeamRequest;
import Core.Enums.Ruolo;
import Core.POJO_Entities.Team;
import Core.POJO_Entities.User;

public class TeamService {
    private final ITeamRepository teamRepository;
    private final IUserRepository userRepository;

    public TeamService(ITeamRepository teamRepository, IUserRepository userRepo) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepo;
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
}
