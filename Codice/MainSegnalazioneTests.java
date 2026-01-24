
import Application.Requests.CreaSegnalazioneRequest;
import Application.Services.SegnalazioneService;
import Presentation.Controllers.SegnalazioneController;
import Presentation.Validators.SegnalazioneValidator;
import Application.IRepositories.IHackathonRepository;
import Application.IRepositories.ISegnalazioneRepository;
import Application.IRepositories.ITeamRepository;
import Application.IRepositories.IUserRepository;
import Application.IRepositories.IPartecipazioneRepository;
import Core.Enums.Ruolo;
import Core.Enums.StatoHackathon;
import Core.POJO_Entities.Hackathon;
import Core.POJO_Entities.Team;
import Core.POJO_Entities.User;
import Infrastructure.Databases.InMemoryDatabase;
import Infrastructure.Repositories.HackathonRepository;
import Infrastructure.Repositories.SegnalazioneRepository;
import Infrastructure.Repositories.TeamRepository;
import Infrastructure.Repositories.UserRepository;
import Infrastructure.Repositories.PartecipazioneRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MainSegnalazioneTests {

    static SegnalazioneController controller;
    static SegnalazioneService service;
    static SegnalazioneValidator validator;
    static IUserRepository userRepo;
    static ITeamRepository teamRepo;
    static IHackathonRepository hackathonRepo;
    static ISegnalazioneRepository segnalazioneRepo;
    static IPartecipazioneRepository partecipazioneRepo;

    static User organizzatore;
    static User giudice;
    static User mentore;
    static User leader;
    static User membro;
    static User nonMentore; // Un altro utente che non è mentore di questo hackathon
    static Team team;
    static Hackathon hackathon;

    public static void main(String[] args) {
        setupEnvironment();

        System.out.println("--- INIZIO TEST SEGNALAZIONI (CONTROLLER) ---\n");

        testSegnalazioneSuccesso();
        testErroreMentoreNonAutorizzato();
        testErroreTeamNonPartecipante();
        testRecuperoSegnalazioniOrganizer();

        System.out.println("\n--- FINE TEST ---");
    }

    private static void setupEnvironment() {
        InMemoryDatabase db = InMemoryDatabase.getInstance();
        userRepo = new UserRepository(db);
        teamRepo = new TeamRepository(db);
        hackathonRepo = new HackathonRepository(db);
        segnalazioneRepo = new SegnalazioneRepository(db);
        partecipazioneRepo = new PartecipazioneRepository(db);

        service = new SegnalazioneService(segnalazioneRepo, hackathonRepo, userRepo, teamRepo, partecipazioneRepo);
        validator = new SegnalazioneValidator();
        controller = new SegnalazioneController(service, validator);

        // Setup Utenti
        organizzatore = new User("Org", "Rossi", "org@test.com", Ruolo.ORGANIZZATORE);
        giudice = new User("Giudice", "Bianchi", "judge@test.com", Ruolo.GIUDICE);
        mentore = new User("Mentore", "Verdi", "mentor@test.com", Ruolo.MENTORE);
        nonMentore = new User("Intruso", "Neri", "intruso@test.com", Ruolo.MENTORE); // È un mentore ma non assegnato
        leader = new User("Leader", "Team", "leader@test.com", Ruolo.LEADER_TEAM);
        membro = new User("Membro", "Team", "membro@test.com", Ruolo.MEMBRO_TEAM);

        userRepo.save(organizzatore);
        userRepo.save(giudice);
        userRepo.save(mentore);
        userRepo.save(nonMentore);
        userRepo.save(leader);
        userRepo.save(membro);

        // Setup Team
        team = new Team("Bravo Team", leader);
        team.getMembri().add(membro);
        teamRepo.save(team);

        // Setup Hackathon
        List<User> mentori = new ArrayList<>();
        mentori.add(mentore);

        hackathon = new Hackathon("CyberSec Hack", "Regole...",
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(2),
                "Online", 5000, 4,
                organizzatore, giudice, mentori, StatoHackathon.IN_CORSO);
        hackathonRepo.save(hackathon);

        // Iscrizione Team all'Hackathon
        Core.POJO_Entities.Partecipazione p = new Core.POJO_Entities.Partecipazione(team, hackathon);
        partecipazioneRepo.save(p);
    }

    private static void testSegnalazioneSuccesso() {
        System.out.println(">> TEST 1: Creazione Segnalazione con Successo");

        CreaSegnalazioneRequest request = new CreaSegnalazioneRequest(
                hackathon.getId(),
                team.getId(),
                mentore.getId(),
                "Il team ha copiato codice da StackOverflow senza citare (scherzo)");

        Object response = controller.creaSegnalazione(request);
        System.out.println("Response: " + response);

        if (response.toString().contains("Successo 200")) {
            System.out.println("   [OK] Risposta corretta.");
            if (!segnalazioneRepo.findAll().isEmpty()) {
                System.out.println("   [OK] Segnalazione persistita nel DB.");
            } else {
                System.out.println("   [FAIL] Segnalazione NON trovata nel DB.");
            }
        } else {
            System.out.println("   [FAIL] Risposta inattesa.");
        }
    }

    private static void testErroreMentoreNonAutorizzato() {
        System.out.println("\n>> TEST 2: Errore Mentore Non Autorizzato (Non assegnato a questo Hackathon)");

        CreaSegnalazioneRequest request = new CreaSegnalazioneRequest(
                hackathon.getId(),
                team.getId(),
                nonMentore.getId(), // ID dell'intruso
                "Violazione grave!");

        Object response = controller.creaSegnalazione(request);
        System.out.println("Response: " + response);

        if (response.toString().contains("Errore 500") && response.toString().contains("non è un mentore")) {
            System.out.println("   [OK] Errore di sicurezza catturato.");
        } else {
            System.out.println("   [FAIL] Il sistema ha permesso a un mentore esterno di segnalare.");
        }
    }

    private static void testErroreTeamNonPartecipante() {
        System.out.println("\n>> TEST 3: Errore Team Non Partecipante");

        // Creiamo un team fantasma che non partecipa
        Team teamFantasma = new Team("Ghost Team", new User("Fantasma", "Casper", "ghost@test.com", Ruolo.LEADER_TEAM));
        teamRepo.save(teamFantasma);
        // NON creiamo la partecipazione

        CreaSegnalazioneRequest request = new CreaSegnalazioneRequest(
                hackathon.getId(),
                teamFantasma.getId(),
                mentore.getId(),
                "Non dovrebbero essere qui.");

        Object response = controller.creaSegnalazione(request);
        System.out.println("Response: " + response);

        if (response.toString().contains("Errore 500") && response.toString().contains("non partecipa")) {
            System.out.println("   [OK] Errore di partecipazione catturato.");
        } else {
            System.out.println("   [FAIL] Segnalazione accettata per un team non iscritto.");
        }
    }

    private static void testRecuperoSegnalazioniOrganizer() {
        System.out.println("\n>> TEST 4: Recupero Segnalazioni da parte dell'Organizzatore");

        Object response = controller.getSegnalazioni(hackathon.getId(), organizzatore.getId());

        if (response instanceof List) {
            List<?> lista = (List<?>) response;
            System.out.println("   [OK] Recuperata una lista di dimensione: " + lista.size());
            if (lista.size() >= 1) {
                System.out.println("   [OK] La lista contiene la segnalazione creata nel Test 1.");

                // VERIFICA AGGIUNTA: Controllo che il Team sia visibile
                Object item = lista.get(0);
                if (item instanceof Core.POJO_Entities.Segnalazione) {
                    Core.POJO_Entities.Segnalazione s = (Core.POJO_Entities.Segnalazione) item;
                    if (s.getTeam() != null && s.getTeam().getId().equals(team.getId())) {
                        System.out.println(
                                "   [OK] Check getTeam(): Il team è correttamente visibile nella segnalazione.");
                    } else {
                        System.out.println("   [FAIL] Check getTeam(): Team non associato o errato.");
                    }
                }

            } else {
                System.out.println("   [FAIL] La lista è vuota.");
            }
        } else {
            System.out.println("Response: " + response);
            System.out.println("   [FAIL] Risposta non è una lista.");
        }

        System.out.println("\n>> TEST 4b: Recupero Negato a Utente Non Organizzatore");
        Object responseErrore = controller.getSegnalazioni(hackathon.getId(), mentore.getId()); // Mentore prova a
                                                                                                // leggere tutto
        System.out.println("Response: " + responseErrore);

        if (responseErrore.toString().contains("Errore 500")
                && responseErrore.toString().contains("Solo l'organizzatore")) {
            System.out.println("   [OK] Accesso negato correttamente.");
        } else {
            System.out.println("   [FAIL] Accesso consentito erroneamente.");
        }
    }
}
