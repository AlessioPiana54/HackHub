import Application.Requests.CreaHackathonRequest;
import Application.Services.HackathonService;
import Core.Enums.Ruolo;
import Core.POJO_Entities.User;
import Infrastructure.Databases.InMemoryDatabase;
import Infrastructure.Repositories.HackathonRepository;
import Infrastructure.Repositories.UserRepository;
import Presentation.Controllers.HackathonController;
import Presentation.Validators.CreaHackathonValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class MainErrorTests {
    
    // Variabili statiche per riutilizzarle nei vari test
    static HackathonController controller;
    static UserRepository userRepo;
    static User organizzatoreValido;
    static User giudiceValido;
    static User utenteSemplice; 

    public static void main(String[] args) {
        setupEnvironment();

        System.out.println("--- INIZIO TEST CASI DI ERRORE ---\n");

        testErroreDateIncoerenti();
        testErroreCampiObbligatori();
        testErroreUtenteNonEsistente();
        testErroreRuoliErrati();
        
        System.out.println("\n--- FINE TEST ---");
    }

    // --- 1. SETUP COMUNE ---
    private static void setupEnvironment() {
        InMemoryDatabase db = InMemoryDatabase.getInstance();
        userRepo = new UserRepository(db);
        HackathonRepository hackathonRepo = new HackathonRepository(db);
        CreaHackathonValidator validator = new CreaHackathonValidator();
        HackathonService service = new HackathonService(hackathonRepo, userRepo);
        controller = new HackathonController(service, validator);

        // Creiamo utenti per i test
        organizzatoreValido = new User("Mario", "Boss", "mario@test.com", Ruolo.ORGANIZZATORE);
        giudiceValido = new User("Luigi", "Law", "luigi@test.com", Ruolo.GIUDICE);
        // Usiamo un ruolo 'sbagliato' per testare i permessi (es. riutilizziamo Giudice come fosse un semplice user se non abbiamo PARTECIPANTE)
        utenteSemplice = new User("Pino", "Partecipante", "pino@test.com", Ruolo.GIUDICE); 

        userRepo.save(organizzatoreValido);
        userRepo.save(giudiceValido);
        userRepo.save(utenteSemplice);
    }

    // --- 2. TEST: DATE INCOERENTI ---
    private static void testErroreDateIncoerenti() {
        System.out.println(">> TEST 1: Date Incoerenti (Scadenza Iscrizioni DOPO Inizio Evento)");
        LocalDateTime now = LocalDateTime.now();

        CreaHackathonRequest request = new CreaHackathonRequest(
            "Hackathon Sbagliato", "Regole...",
            now.plusDays(1),    // Iscrizioni aprono domani
            now.plusDays(10),   // Iscrizioni chiudono tra 10 giorni
            now.plusDays(5),    // MA l'evento inizia tra 5 giorni! (ERRORE)
            now.plusDays(6),    // Fine evento
            "Online", 1000, 4,
            organizzatoreValido.getId(), giudiceValido.getId(), new ArrayList<>()
        );

        Object response = controller.creaHackathon(request);
        stampaEsito(response); // Ci aspettiamo errore dal Validator
    }

    // --- 3. TEST: CAMPI OBBLIGATORI / VALORI NEGATIVI ---
    private static void testErroreCampiObbligatori() {
        System.out.println(">> TEST 2: Campi Invalidi (Nome Vuoto e Premio Negativo)");
        LocalDateTime now = LocalDateTime.now();

        CreaHackathonRequest request = new CreaHackathonRequest(
            "",                 // ERRORE: Nome vuoto
            "Regole...",
            now.plusDays(1), now.plusDays(2), now.plusDays(3), now.plusDays(4),
            "Online", 
            -500.0,             // ERRORE: Premio negativo
            4,
            organizzatoreValido.getId(), giudiceValido.getId(), new ArrayList<>()
        );

        Object response = controller.creaHackathon(request);
        stampaEsito(response);
    }

    // --- 4. TEST: UTENTE NON TROVATO (404 Logic) ---
    private static void testErroreUtenteNonEsistente() {
        System.out.println(">> TEST 3: Utente Organizzatore non presente nel DB");
        LocalDateTime now = LocalDateTime.now();

        CreaHackathonRequest request = new CreaHackathonRequest(
            "Ghost Hackathon", "Regole...",
            now.plusDays(1), now.plusDays(2), now.plusDays(3), now.plusDays(4),
            "Online", 1000, 4,
            "ID-INESISTENTE-999", // ID Inventato
            giudiceValido.getId(), 
            new ArrayList<>()
        );

        Object response = controller.creaHackathon(request);
        stampaEsito(response); // Ci aspettiamo errore dal Service
    }

    // --- 5. TEST: RUOLO SBAGLIATO (Security Logic) ---
    private static void testErroreRuoliErrati() {
        System.out.println(">> TEST 4: Ruolo Errato (Un Giudice prova a fare l'Organizzatore)");
        LocalDateTime now = LocalDateTime.now();

        // Passiamo l'ID di 'utenteSemplice' (che è GIUDICE) nel campo Organizzatore
        CreaHackathonRequest request = new CreaHackathonRequest(
            "Hacker Attack", "Regole...",
            now.plusDays(1), now.plusDays(2), now.plusDays(3), now.plusDays(4),
            "Online", 1000, 4,
            utenteSemplice.getId(), // ERRORE: Lui non ha ruolo ORGANIZZATORE
            giudiceValido.getId(), 
            new ArrayList<>()
        );

        Object response = controller.creaHackathon(request);
        stampaEsito(response); // Ci aspettiamo "SecurityException" gestita
    }

    // Helper per stampare colorato o formattato
    private static void stampaEsito(Object response) {
        String res = response.toString();
        if (res.startsWith("Errore")) {
            System.out.println("   [OK - Errore rilevato correttamente]: " + res + "\n");
        } else {
            System.out.println("   [FALLITO - Operazione riuscita inaspettatamente]: " + res + "\n");
        }
    }
}
