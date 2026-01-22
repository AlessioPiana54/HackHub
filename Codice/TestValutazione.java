
import Application.Requests.CreaValutazioneRequest;
import Application.Services.SottomissioneService;
import Core.Enums.Ruolo;
import Core.Enums.StatoHackathon;
import Core.POJO_Entities.Hackathon;
import Core.POJO_Entities.Sottomissione;
import Core.POJO_Entities.Team;
import Core.POJO_Entities.User;
import Infrastructure.Databases.InMemoryDatabase;
import Infrastructure.Repositories.HackathonRepository;
import Infrastructure.Repositories.SottomissioneRepository;
import Infrastructure.Repositories.TeamRepository;
import Infrastructure.Repositories.UserRepository;
import Presentation.Controllers.SottomissioneController;
import Presentation.Validators.SottomissioneValidator;
import Presentation.Validators.ValutazioneValidator;
import java.time.LocalDateTime;

public class TestValutazione {

    static SottomissioneController controller;
    static SottomissioneService service;
    static SottomissioneValidator sottomissioneValidator;
    static ValutazioneValidator valutazioneValidator;
    static UserRepository userRepo;
    static TeamRepository teamRepo;
    static HackathonRepository hackathonRepo;
    static SottomissioneRepository sottomissioneRepo;
    static Infrastructure.Repositories.PartecipazioneRepository partecipazioneRepo;

    static User giudice;
    static User leader;
    static Team team;
    static Hackathon hackathon;
    static Sottomissione sottomissione;

    public static void main(String[] args) {
        setup();
        System.out.println("--- INIZIO TEST VALUTAZIONE (SOLO CREAZIONE) ---\n");

        testValutazioneSuccesso();
        testErroreGiaValutata();

        System.out.println("\n--- FINE TEST ---");
    }

    private static void setup() {
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

        // Giudice
        giudice = new User("Giudice", "Dredd", "dredd@law.com", Ruolo.GIUDICE);
        userRepo.save(giudice);

        // Leader e Team
        leader = new User("Dev", "Eloper", "dev@code.com", Ruolo.LEADER_TEAM);
        userRepo.save(leader);
        team = new Team("Lambda Team", leader);
        teamRepo.save(team);

        // Hackathon
        hackathon = new Hackathon("HackTest", "Rules", LocalDateTime.now(), LocalDateTime.now().plusDays(2),
                LocalDateTime.now(), LocalDateTime.now().plusDays(5), "Web", 1000, 3,
                new User("Org", "Org", "o@o.com", Ruolo.ORGANIZZATORE), giudice, null, StatoHackathon.IN_VALUTAZIONE);
        hackathonRepo.save(hackathon);

        // Create Partecipazione first
        Core.POJO_Entities.Partecipazione p = new Core.POJO_Entities.Partecipazione(team, hackathon);
        partecipazioneRepo.save(p);

        // Sottomissione
        sottomissione = new Sottomissione(p, leader, "http://repo", "My Project");
        sottomissioneRepo.save(sottomissione);
    }

    private static void testValutazioneSuccesso() {
        System.out.println(">> Test 1: Nuova Valutazione Successo");
        CreaValutazioneRequest req = new CreaValutazioneRequest(
                sottomissione.getId(),
                giudice.getId(),
                8.5,
                "Ottimo lavoro.");

        Object res = controller.valutaSottomissione(req);
        System.out.println("Response: " + res);

        Sottomissione check = sottomissioneRepo.findById(sottomissione.getId());
        if (check.getValutazione() != null && check.getValutazione().getVoto() == 8.5) {
            System.out.println("   [OK] Valutazione salvata.");
        } else {
            System.out.println("   [FAIL] Valutazione non salvata.");
        }
    }

    private static void testErroreGiaValutata() {
        System.out.println("\n>> Test 2: Errore Già Valutata");
        // Proviamo a valutare di nuovo la stessa sottomissione
        CreaValutazioneRequest req = new CreaValutazioneRequest(
                sottomissione.getId(),
                giudice.getId(),
                10.0,
                "Voglio cambiarti il voto.");

        Object res = controller.valutaSottomissione(req);
        System.out.println("Response: " + res);

        if (res.toString().contains("Errore 500") && res.toString().contains("già stata valutata")) {
            System.out.println("   [OK] Errore duplicazione catturato.");
        } else {
            System.out.println("   [FAIL] Errore NON catturato (forse ha sovrascritto?).");
        }
    }
}
