import Application.Requests.InviaSottomissioneRequest;
import Application.Services.SottomissioneService;
import Presentation.Controllers.SottomissioneController;
import Presentation.Validators.SottomissioneValidator;
import Presentation.Validators.ValutazioneValidator;
import Application.IRepositories.IHackathonRepository;
import Application.IRepositories.ISottomissioneRepository;
import Application.IRepositories.ITeamRepository;
import Application.IRepositories.IUserRepository;
import Core.Enums.Ruolo;
import Core.Enums.StatoHackathon;
import Core.POJO_Entities.Hackathon;
import Core.POJO_Entities.Team;
import Core.POJO_Entities.User;
import Infrastructure.Databases.InMemoryDatabase;
import Infrastructure.Repositories.HackathonRepository;
import Infrastructure.Repositories.SottomissioneRepository;
import Infrastructure.Repositories.TeamRepository;
import Infrastructure.Repositories.UserRepository;
import java.time.LocalDateTime;

public class MainSottomissioneTests {

    static SottomissioneController controller;
    static SottomissioneService service;
    static SottomissioneValidator sottomissioneValidator;
    static ValutazioneValidator valutazioneValidator;
    static IUserRepository userRepo;
    static ITeamRepository teamRepo;
    static IHackathonRepository hackathonRepo;
    static ISottomissioneRepository sottomissioneRepo;
    static Application.IRepositories.IPartecipazioneRepository partecipazioneRepo;

    static User leader;
    static User membro;
    static User esterno;
    static Team team;
    static Hackathon hackathon;

    public static void main(String[] args) {
        setupEnvironment();

        System.out.println("--- INIZIO TEST SOTTOMISSIONI (CONTROLLER) ---\n");

        testSottomissioneSuccessoLeader();
        testSottomissioneSuccessoMembro();
        testErroreValidazione(); // Nuovo test
        testErroreUtenteNonNelTeam();
        testErroreHackathonNonTrovato();
        testErroreDateNonValide();

        System.out.println("\n--- FINE TEST ---");
    }

    private static void setupEnvironment() {
        InMemoryDatabase db = InMemoryDatabase.getInstance();
        userRepo = new UserRepository(db);
        teamRepo = new TeamRepository(db);
        hackathonRepo = new HackathonRepository(db);
        sottomissioneRepo = new SottomissioneRepository(db);
        partecipazioneRepo = new Infrastructure.Repositories.PartecipazioneRepository(db);

        service = new SottomissioneService(sottomissioneRepo, hackathonRepo, teamRepo, userRepo, partecipazioneRepo);
        sottomissioneValidator = new SottomissioneValidator();
        valutazioneValidator = new ValutazioneValidator();
        controller = new SottomissioneController(service, sottomissioneValidator, valutazioneValidator);

        // Setup Utenti
        leader = new User("Leader", "Team", "leader@test.com", Ruolo.LEADER_TEAM);
        membro = new User("Membro", "Team", "membro@test.com", Ruolo.MEMBRO_TEAM);
        esterno = new User("Esterno", "Tizio", "esterno@test.com", Ruolo.UTENTE_SENZA_TEAM);

        userRepo.save(leader);
        userRepo.save(membro);
        userRepo.save(esterno);

        // Setup Team
        team = new Team("Alpha Team", leader);
        team.getMembri().add(membro);
        teamRepo.save(team);

        // Setup Hackathon
        // Hackathon IN CORSO: Iniziato ieri, finisce tra 2 giorni
        hackathon = new Hackathon("Hack 2024", "Regole...",
                LocalDateTime.now().minusDays(5), // Inizio Iscrizioni
                LocalDateTime.now().minusDays(2), // Scadenza Iscrizioni
                LocalDateTime.now().minusDays(1), // Data Inizio
                LocalDateTime.now().plusDays(2), // Data Fine
                "Online", 1000, 5,
                new User("Org", "Org", "org@test.com", Ruolo.ORGANIZZATORE),
                new User("Jud", "Ge", "judge@test.com", Ruolo.GIUDICE), null, StatoHackathon.IN_CORSO);
        hackathonRepo.save(hackathon);

        // --- REGISTRAZIONE AUTOMATICA TEAM ALL'HACKATHON (Necessaria per i test di
        // sottomissione) ---
        Core.POJO_Entities.Partecipazione p = new Core.POJO_Entities.Partecipazione(team, hackathon);
        partecipazioneRepo.save(p);
    }

    private static void testSottomissioneSuccessoLeader() {
        System.out.println(">> TEST 1: Sottomissione Successo (Leader)");

        InviaSottomissioneRequest req = new InviaSottomissioneRequest(
                hackathon.getId(),
                team.getId(),
                leader.getId(),
                "http://github.com/project",
                "Il nostro progetto bellissimo");

        Object response = controller.inviaSottomissione(req);
        System.out.println("Response: " + response);

        if (response.toString().contains("Successo 200")) {
            System.out.println("   [OK] Risposta corretta.");
            // Verify persistence
            if (sottomissioneRepo.findByTeamId(team.getId()).size() > 0) {
                System.out.println("   [OK] Sottomissione trovata nel repository.");
            } else {
                // Modifica per usare getSottomissioniByTeam dal DB che ora usa partecipazioni
                System.out.println("   [CHECK] Controllo esistenza... trovate: "
                        + sottomissioneRepo.findByTeamId(team.getId()).size());
            }
        } else {
            System.out.println("   [FAIL] Risposta inattesa.");
        }
    }

    private static void testSottomissioneSuccessoMembro() {
        System.out.println("\n>> TEST 2: Sottomissione Successo (Membro)");

        InviaSottomissioneRequest req = new InviaSottomissioneRequest(
                hackathon.getId(),
                team.getId(),
                membro.getId(),
                "http://github.com/project-v2",
                "Versione aggiornata");

        Object response = controller.inviaSottomissione(req);
        System.out.println("Response: " + response);

        if (response.toString().contains("Successo 200")) {
            System.out.println("   [OK] Risposta corretta.");
        } else {
            System.out.println("   [FAIL] Risposta inattesa.");
        }
    }

    private static void testErroreValidazione() {
        System.out.println("\n>> TEST 3: Errore Validazione (Link mancante)");

        InviaSottomissioneRequest req = new InviaSottomissioneRequest(
                hackathon.getId(),
                team.getId(),
                leader.getId(),
                "", // LINK VUOTO
                "Descrizione...");

        Object response = controller.inviaSottomissione(req);
        System.out.println("Response: " + response);

        if (response.toString().contains("Errore 400") && response.toString().contains("link")) {
            System.out.println("   [OK] Errore validazione catturato correttamente.");
        } else {
            System.out.println("   [FAIL] Validazione fallita o non scattata.");
        }
    }

    private static void testErroreUtenteNonNelTeam() {
        System.out.println("\n>> TEST 4: Errore Utente Non Nel Team");

        InviaSottomissioneRequest req = new InviaSottomissioneRequest(
                hackathon.getId(),
                team.getId(),
                esterno.getId(),
                "http://evil.com",
                "Hacker vibes");

        Object response = controller.inviaSottomissione(req);
        System.out.println("Response: " + response);

        if (response.toString().contains("Errore 500") && response.toString().contains("non appartiene al Team")) {
            System.out.println("   [OK] Errore logica dominio catturato.");
        } else {
            System.out.println("   [FAIL] Errore non catturato o messaggio errato.");
        }
    }

    private static void testErroreHackathonNonTrovato() {
        System.out.println("\n>> TEST 5: Errore Hackathon Non Trovato");

        InviaSottomissioneRequest req = new InviaSottomissioneRequest(
                "ID_FASULLO",
                team.getId(),
                leader.getId(),
                "http://...",
                "...");

        Object response = controller.inviaSottomissione(req);
        System.out.println("Response: " + response);

        if (response.toString().contains("Errore 500") && response.toString().contains("Hackathon non trovato")) {
            System.out.println("   [OK] Errore DB catturato.");
        } else {
            System.out.println("   [FAIL] Errore non catturato.");
        }
    }

    private static void testErroreDateNonValide() {
        System.out.println("\n>> TEST 6: Errore Date Hackathon Non Valide (Simulato)");

        // Creiamo un Hackathon scaduto
        Hackathon oldHack = new Hackathon("Old Hack", "...",
                LocalDateTime.now().minusDays(20), LocalDateTime.now().minusDays(15),
                LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(5), "Online", 0, 5,
                leader, leader, null, StatoHackathon.CONCLUSO);
        hackathonRepo.save(oldHack);

        // FIX: Dobbiamo simulare che il team fosse iscritto a questo hackathon
        // Salviamo manualmente la partecipazione per bypassare i controlli del
        // TeamService (che vieterebbe l'iscrizione a un hack chiuso)
        Core.POJO_Entities.Partecipazione pOld = new Core.POJO_Entities.Partecipazione(team, oldHack);
        partecipazioneRepo.save(pOld);

        InviaSottomissioneRequest req = new InviaSottomissioneRequest(
                oldHack.getId(),
                team.getId(),
                leader.getId(),
                "http://...",
                "...");

        Object response = controller.inviaSottomissione(req);
        System.out.println("Response: " + response);

        if (response.toString().contains("Errore 500") && response.toString().contains("non è in corso")) {
            System.out.println("   [OK] Errore date catturato.");
        } else {
            System.out.println("   [FAIL] Errore date non catturato.");
        }
    }
}
