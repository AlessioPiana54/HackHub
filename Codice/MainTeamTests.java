import Application.Requests.CreaTeamRequest;
import Application.Services.TeamService;
import Core.Enums.Ruolo;
import Core.POJO_Entities.User;
import Infrastructure.Databases.InMemoryDatabase;
import Infrastructure.Repositories.TeamRepository;
import Infrastructure.Repositories.UserRepository;
import Presentation.Controllers.TeamController;
import Presentation.Validators.TeamValidator;

public class MainTeamTests {

    static TeamController controller;
    static UserRepository userRepo;
    static TeamRepository teamRepo;
    static Infrastructure.Repositories.HackathonRepository hackathonRepo;
    static Infrastructure.Repositories.PartecipazioneRepository partecipazioneRepo;
    static User utenteSenzaTeam;
    static User utenteGiaInTeam;

    public static void main(String[] args) {
        setupEnvironment();

        System.out.println("--- INIZIO TEST CREAZIONE TEAM ---\n");

        testCreazioneTeamSuccesso();
        testErroreUtenteNonEsistente();
        testErroreRuoloErrato();
        testErroreNomeDuplicato();

        System.out.println("\n--- FINE TEST ---");
    }

    private static void setupEnvironment() {
        InMemoryDatabase db = InMemoryDatabase.getInstance();
        userRepo = new UserRepository(db);
        teamRepo = new TeamRepository(db);
        hackathonRepo = new Infrastructure.Repositories.HackathonRepository(db);
        partecipazioneRepo = new Infrastructure.Repositories.PartecipazioneRepository(db);

        TeamValidator validator = new TeamValidator();
        TeamService service = new TeamService(teamRepo, userRepo, hackathonRepo, partecipazioneRepo);
        controller = new TeamController(service, validator);

        // Setup Utenti
        utenteSenzaTeam = new User("Marco", "Rossi", "marco@test.com", Ruolo.UTENTE_SENZA_TEAM);
        utenteGiaInTeam = new User("Luca", "Verdi", "luca@test.com", Ruolo.MEMBRO_TEAM);

        userRepo.save(utenteSenzaTeam);
        userRepo.save(utenteGiaInTeam);
    }

    // TEST 1: Successo
    private static void testCreazioneTeamSuccesso() {
        System.out.println(">> TEST 1: Creazione Team con Successo");

        CreaTeamRequest request = new CreaTeamRequest(
                "Super Team",
                utenteSenzaTeam.getId());

        Object response = controller.creaTeam(request);
        System.out.println("Response: " + response);

        if (response.toString().contains("Successo 200")) {
            System.out.println("   [OK] Risposta corretta.");
        } else {
            System.out.println("   [FAIL] Risposta inattesa.");
        }

        // Verifica aggiornamento Ruolo
        User updatedUser = userRepo.findById(utenteSenzaTeam.getId());
        if (updatedUser.getRuolo() == Ruolo.LEADER_TEAM) {
            System.out.println("   [OK] Ruolo utente aggiornato a LEADER_TEAM.");
        } else {
            System.out.println("   [FAIL] Ruolo utente NON aggiornato: " + updatedUser.getRuolo());
        }
    }

    // TEST 2: Utente Non Esistente
    private static void testErroreUtenteNonEsistente() {
        System.out.println("\n>> TEST 2: Utente Non Esistente");

        CreaTeamRequest request = new CreaTeamRequest(
                "Ghost Team",
                "ID-NON-ESISTENTE");

        Object response = controller.creaTeam(request);
        System.out.println("Response: " + response);

        if (response.toString().contains("Errore 500") || response.toString().contains("non trovato")) {
            System.out.println("   [OK] Errore rilevato correttamente.");
        } else {
            System.out.println("   [FAIL] Errore non rilevato.");
        }
    }

    // TEST 3: Ruolo Errato
    private static void testErroreRuoloErrato() {
        System.out.println("\n>> TEST 3: Utente con Ruolo Errato");

        CreaTeamRequest request = new CreaTeamRequest(
                "Wrong Team",
                utenteGiaInTeam.getId());

        Object response = controller.creaTeam(request);
        System.out.println("Response: " + response);

        if (response.toString().contains("Errore 500") && response.toString().contains("permessi")) {
            System.out.println("   [OK] Permesso negato correttamente.");
        } else {
            System.out.println("   [FAIL] Errore permessi non rilevato.");
        }
    }

    // TEST 4: Nome Duplicato
    private static void testErroreNomeDuplicato() {
        System.out.println("\n>> TEST 4: Nome Duplicato");

        // Usiamo un altro utente per evitare blocco sul ruolo, o riusiamo l'utente base
        // se il test precedente non ha persistito side effects gravi (ma qui ha
        // aggiornato il ruolo...)
        // Creiamo un nuovo utente veloce per questo test
        User altroUtente = new User("Test", "Dup", "dup@test.com", Ruolo.UTENTE_SENZA_TEAM);
        userRepo.save(altroUtente);

        CreaTeamRequest request = new CreaTeamRequest(
                "Super Team", // Stesso nome del Test 1
                altroUtente.getId());

        Object response = controller.creaTeam(request);
        System.out.println("Response: " + response);

        if (response.toString().contains("Errore 500") && response.toString().contains("Esiste già un Team")) {
            System.out.println("   [OK] Duplicato rilevato correttamente.");
        } else {
            System.out.println("   [FAIL] Duplicato NON rilevato.");
        }
    }
}
