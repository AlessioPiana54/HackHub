package Application.Services;

import Application.IRepositories.IHackathonRepository;
import Application.IRepositories.ISottomissioneRepository;
import Application.IRepositories.ITeamRepository;
import Application.IRepositories.IUserRepository;
import Application.Requests.CreaValutazioneRequest;
import Application.Requests.InviaSottomissioneRequest;
import Core.Enums.Ruolo;
import Core.POJO_Entities.Hackathon;
import Core.POJO_Entities.Partecipazione;
import Core.POJO_Entities.Sottomissione;
import Core.POJO_Entities.Team;
import Core.POJO_Entities.User;
import Core.POJO_Entities.Valutazione;
import java.time.LocalDateTime;
import java.util.List;

public class SottomissioneService {
    private ISottomissioneRepository sottomissioneRepo;
    private IHackathonRepository hackathonRepo;
    private ITeamRepository teamRepo;
    private IUserRepository userRepo;
    private Application.IRepositories.IPartecipazioneRepository partecipazioneRepo;

    public SottomissioneService(ISottomissioneRepository sottomissioneRepo, IHackathonRepository hackathonRepo,
            ITeamRepository teamRepo, IUserRepository userRepo,
            Application.IRepositories.IPartecipazioneRepository partecipazioneRepo) {
        this.sottomissioneRepo = sottomissioneRepo;
        this.hackathonRepo = hackathonRepo;
        this.teamRepo = teamRepo;
        this.userRepo = userRepo;
        this.partecipazioneRepo = partecipazioneRepo;
    }

    public Sottomissione inviaSottomissione(InviaSottomissioneRequest request) {
        // 1. Validazione (Input formale già validato dal Validator)

        // 2. Recupero Entità
        Hackathon hackathon = hackathonRepo.findById(request.getIdHackathon());
        if (hackathon == null) {
            throw new IllegalArgumentException("Hackathon non trovato.");
        }

        Team team = teamRepo.findById(request.getIdTeam());
        if (team == null) {
            throw new IllegalArgumentException("Team non trovato.");
        }

        User user = userRepo.findById(request.getIdUtente());
        if (user == null) {
            throw new IllegalArgumentException("Utente non trovato.");
        }

        // 3. Validazione Regole di Business
        // - L'utente deve essere membro o leader del team
        boolean isMembro = team.getMembri().stream()
                .anyMatch(u -> u.getId().equals(user.getId()));
        if (!isMembro) {
            throw new IllegalArgumentException("L'utente non appartiene al Team specificato.");
        }

        // Ulteriore controllo ruolo generico
        if (user.getRuolo() != Ruolo.MEMBRO_TEAM && user.getRuolo() != Ruolo.LEADER_TEAM) {
            throw new IllegalArgumentException("Solo membri o leader di un team possono inviare sottomissioni.");
        }

        // Controllo Partecipazione attiva per questo Hackathon e Team
        List<Partecipazione> partecipazioni = partecipazioneRepo
                .findByTeamId(team.getId());
        Partecipazione partecipazioneAttiva = partecipazioni.stream()
                .filter(p -> p.getHackathon().getId().equals(hackathon.getId()))
                .findFirst()
                .orElse(null);

        if (partecipazioneAttiva == null) {
            throw new IllegalArgumentException("Il team non è iscritto a questo Hackathon.");
        }

        // Controllo stato e date Hackathon
        LocalDateTime now = LocalDateTime.now();
        if (hackathon.getStato() != Core.Enums.StatoHackathon.IN_CORSO ||
                now.isBefore(hackathon.getDataInizio()) ||
                now.isAfter(hackathon.getDataFine())) {

            throw new IllegalArgumentException("Le sottomissioni sono chiuse o l'Hackathon non è in corso.");
        }

        // 4. Creazione Sottomissione
        Sottomissione sottomissione = new Sottomissione(
                partecipazioneAttiva,
                user,
                request.getLinkProgetto(),
                request.getDescrizione());

        // 5. Salvataggio
        sottomissioneRepo.save(sottomissione);

        return sottomissione;
    }

    public Sottomissione valutaSottomissione(CreaValutazioneRequest request) {
        // 1. Recupero Entità
        Sottomissione sottomissione = sottomissioneRepo.findById(request.getIdSottomissione());
        if (sottomissione == null) {
            throw new IllegalArgumentException("Sottomissione non trovata.");
        }

        User giudice = userRepo.findById(request.getIdGiudice());
        if (giudice == null) {
            throw new IllegalArgumentException("Giudice non trovato.");
        }

        Hackathon hackathon = sottomissione.getPartecipazione().getHackathon();

        // 2. Controllo che l'utente sia effettivamente il Giudice dell'Hackathon
        if (hackathon.getGiudice() == null || !hackathon.getGiudice().getId().equals(giudice.getId())) {
            throw new IllegalArgumentException("L'utente specificato non è il giudice di questo Hackathon.");
        }

        // 3. Controllo che lo Stato dell'Hackathon sia "IN_VALUTAZIONE"
        if (hackathon.getStato() != Core.Enums.StatoHackathon.IN_VALUTAZIONE) {
            throw new IllegalArgumentException("L'Hackathon non è in stato di valutazione.");
        }

        // 4. Controllo che la Sottomissione non abbia già una Valutazione
        if (sottomissione.getValutazione() != null) {
            throw new IllegalArgumentException("Questa sottomissione è già stata valutata. Impossibile sovrascrivere.");
        }

        // 5. Creazione di una NUOVA istanza di Valutazione
        Valutazione nuovaValutazione = new Valutazione(giudice, request.getGiudizio(), request.getVoto());
        sottomissione.setValutazione(nuovaValutazione);

        // 6. Salvataggio
        // Poiché Sottomissione è l'Aggregate Root, salviamo solo Sottomissione.
        sottomissioneRepo.edit(sottomissione);

        return sottomissione;
    }
}
