package Application.Services;

import Application.IRepositories.IHackathonRepository;
import Application.IRepositories.IUserRepository;
import Application.Requests.CreaHackathonRequest;
import Core.Enums.Ruolo;
import Core.Enums.StatoHackathon;
import Core.POJO_Entities.Hackathon;
import Core.POJO_Entities.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HackathonService {

    private final IHackathonRepository hackathonRepository;
    private final IUserRepository userRepository;

    public HackathonService(IHackathonRepository hackathonRepo, IUserRepository userRepo) {
        this.hackathonRepository = hackathonRepo;
        this.userRepository = userRepo;
    }

    public Hackathon creaHackathon(CreaHackathonRequest request) {
        // NOTA: I controlli formali (date, stringhe vuote) sono già stati fatti dal
        // Validator.

        // 1. Recupero Utenti (Logica che richiede il DB, quindi rimane nel Service)
        User organizzatore = userRepository.findById(request.getIdOrganizzatore());
        User giudice = userRepository.findById(request.getIdGiudice());

        // 2. Validazione Esistenza (Domain Logic)
        if (organizzatore == null) {
            throw new IllegalArgumentException("Organizzatore non trovato nel database.");
        }
        if (giudice == null) {
            throw new IllegalArgumentException("Giudice non trovato nel database.");
        }

        // 3. Validazione Ruoli (Business Logic pura)
        if (organizzatore.getRuolo() != Ruolo.ORGANIZZATORE) {
            throw new SecurityException("L'utente specificato come organizzatore non ha i permessi necessari.");
        }
        if (giudice.getRuolo() != Ruolo.GIUDICE) {
            throw new IllegalArgumentException("L'utente specificato come giudice non ha il ruolo di GIUDICE.");
        }

        // 4. Validazione Mentori
        List<User> listaMentori = new ArrayList<>();
        for (String idMentore : request.getIdMentori()) {
            User mentore = userRepository.findById(idMentore);
            // Validazione Esistenza
            if (mentore == null) {
                throw new IllegalArgumentException("Mentore con ID " + idMentore + " non trovato.");
            }
            // Validazione Ruolo
            if (mentore.getRuolo() != Ruolo.MENTORE) {
                throw new IllegalArgumentException(
                        "L'utente " + mentore.getNome() + " non ha il ruolo di MENTORE.");
            }
            listaMentori.add(mentore);
        }

        // 5. Calcolo dello Stato Iniziale
        // Se la data di inizio iscrizioni è ORA o nel PASSATO -> IN_ISCRIZIONE
        // Se la data di inizio iscrizioni è nel FUTURO -> IN_ATTESA
        StatoHackathon statoIniziale;
        if (!request.getInizioIscrizioni().isAfter(LocalDateTime.now())) {
            statoIniziale = StatoHackathon.IN_ISCRIZIONE;
        } else {
            statoIniziale = StatoHackathon.IN_ATTESA;
        }

        // 6. Creazione Entità
        // Mappiamo la Request nell'Entità di dominio
        Hackathon nuovoHackathon = new Hackathon(
                request.getNome(),
                request.getRegolamento(),
                request.getInizioIscrizioni(),
                request.getScadenzaIscrizioni(),
                request.getDataInizio(),
                request.getDataFine(),
                request.getLuogo(),
                request.getPremioInDenaro(),
                request.getDimensioneMaxTeam(),
                organizzatore,
                giudice,
                listaMentori,
                statoIniziale);

        // 5. Persistenza
        hackathonRepository.save(nuovoHackathon);

        return nuovoHackathon;
    }

    public List<Hackathon> visualizzaTutti() {
        return hackathonRepository.findAll();
    }
}
