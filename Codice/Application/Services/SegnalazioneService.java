package Application.Services;

import java.util.List;
import Application.IRepositories.IHackathonRepository;
import Application.IRepositories.IPartecipazioneRepository;
import Application.IRepositories.ISegnalazioneRepository;
import Application.IRepositories.ITeamRepository;
import Application.IRepositories.IUserRepository;
import Application.Requests.CreaSegnalazioneRequest;
import Core.POJO_Entities.Hackathon;
import Core.POJO_Entities.Partecipazione;
import Core.POJO_Entities.Segnalazione;
import Core.POJO_Entities.Team;
import Core.POJO_Entities.User;

public class SegnalazioneService {
    private final ISegnalazioneRepository segnalazioneRepo;
    private final IHackathonRepository hackathonRepo;
    private final IUserRepository userRepo;
    private final ITeamRepository teamRepo;
    private final IPartecipazioneRepository partecipazioneRepo;

    public SegnalazioneService(ISegnalazioneRepository segnalazioneRepo,
            IHackathonRepository hackathonRepo,
            IUserRepository userRepo,
            ITeamRepository teamRepo,
            IPartecipazioneRepository partecipazioneRepo) {
        this.segnalazioneRepo = segnalazioneRepo;
        this.hackathonRepo = hackathonRepo;
        this.userRepo = userRepo;
        this.teamRepo = teamRepo;
        this.partecipazioneRepo = partecipazioneRepo;
    }

    public Segnalazione creaSegnallazione(CreaSegnalazioneRequest request) {
        User mentore = userRepo.findById(request.getIdMentore());
        Hackathon hackathon = hackathonRepo.findById(request.getIdHackathon());
        Team team = teamRepo.findById(request.getIdTeam());

        if (mentore == null) {
            throw new IllegalArgumentException("Mentore non trovato: " + request.getIdMentore());
        }
        if (hackathon == null) {
            throw new IllegalArgumentException("Hackathon non trovato: " + request.getIdHackathon());
        }
        if (team == null) {
            throw new IllegalArgumentException("Team non trovato: " + request.getIdTeam());
        }

        // Verifica che l'utente sia effettivamente un mentore dell'hackathon
        boolean isMentor = hackathon.getMentori().stream()
                .anyMatch(m -> m.getId().equals(mentore.getId()));
        if (!isMentor) {
            throw new SecurityException("L'utente " + mentore.getNome() + " non è un mentore per questo Hackathon.");
        }

        // Verifica partecipazione del team all'hackathon
        List<Partecipazione> partecipazioniTeam = partecipazioneRepo.findByTeamId(team.getId());
        Partecipazione partecipazione = partecipazioniTeam.stream()
                .filter(p -> p.getHackathon().getId().equals(hackathon.getId()))
                .findFirst()
                .orElse(null);
        if (partecipazione == null) {
            throw new IllegalArgumentException(
                    "Il Team " + team.getNomeTeam() + " non partecipa all'Hackathon " + hackathon.getNome());
        }

        // Creazione Segnalazione
        Segnalazione segnalazione = new Segnalazione(partecipazione, mentore, request.getDescrizione());
        segnalazioneRepo.save(segnalazione);

        return segnalazione;
    }

    public List<Segnalazione> getSegnalazioni(String idHackathon, String idOrganizer) {
        Hackathon h = hackathonRepo.findById(idHackathon);
        if (h == null)
            throw new IllegalArgumentException("Hackathon non trovato");

        // Verifica permesso Organizer
        if (!h.getOrganizzatore().getId().equals(idOrganizer)) {
            throw new SecurityException("Solo l'organizzatore può vedere le segnalazioni.");
        }

        return segnalazioneRepo.findByHackathonId(idHackathon);
    }
}
