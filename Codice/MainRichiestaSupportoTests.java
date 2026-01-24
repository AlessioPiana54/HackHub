import Application.Requests.CreaRichiestaSupportoRequest;
import Application.Services.RichiestaSupportoService;
import Presentation.Controllers.RichiestaSupportoController;
import Presentation.Validators.RichiestaSupportoValidator;
import Application.IRepositories.IHackathonRepository;
import Application.IRepositories.IRichiestaSupportoRepository;
import Application.IRepositories.ITeamRepository;
import Application.IRepositories.IUserRepository;
import Application.IRepositories.IPartecipazioneRepository;
import Core.Enums.Ruolo;
import Core.Enums.StatoHackathon;
import Core.POJO_Entities.Hackathon;
import Core.POJO_Entities.Team;
import Core.POJO_Entities.User;
import Core.POJO_Entities.Partecipazione;
import Infrastructure.Databases.InMemoryDatabase;
import Infrastructure.Repositories.HackathonRepository;
import Infrastructure.Repositories.RichiestaSupportoRepository;
import Infrastructure.Repositories.TeamRepository;
import Infrastructure.Repositories.UserRepository;
import Infrastructure.Repositories.PartecipazioneRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MainRichiestaSupportoTests {

    static RichiestaSupportoController controller;
    static RichiestaSupportoService service;
    static RichiestaSupportoValidator validator;
    static IUserRepository userRepo;
    static ITeamRepository teamRepo;
    static IHackathonRepository hackathonRepo;
    static IRichiestaSupportoRepository richiestaRepo;
    static IPartecipazioneRepository partecipazioneRepo;

    static User organizzatore, mentore, leader, membro, esterno;
    static Team team;
    static Hackathon hackathon;

    public static void main(String[] args) {
        setupEnvironment();

        System.out.println("--- INIZIO TEST RICHIESTA SUPPORTO ---\n");

        testRichiestaSuccesso();
        testErroreNonMembro();
        testErroreNonPartecipante();

        System.out.println("\n--- FINE TEST ---");
    }

    private static void setupEnvironment() {
        InMemoryDatabase db = InMemoryDatabase.getInstance();
        userRepo = new UserRepository(db);
        teamRepo = new TeamRepository(db);
        hackathonRepo = new HackathonRepository(db);
        richiestaRepo = new RichiestaSupportoRepository(db);
        partecipazioneRepo = new PartecipazioneRepository(db);

        validator = new RichiestaSupportoValidator();
        service = new RichiestaSupportoService(richiestaRepo, partecipazioneRepo, userRepo);
        controller = new RichiestaSupportoController(service, validator);

        // Users
        organizzatore = new User("Org", "Rossi", "org@test.com", Ruolo.ORGANIZZATORE);
        mentore = new User("Mentore", "Verdi", "mentor@test.com", Ruolo.MENTORE);
        leader = new User("Leader", "Team", "leader@test.com", Ruolo.LEADER_TEAM);
        membro = new User("Membro", "Team", "membro@test.com", Ruolo.MEMBRO_TEAM);
        esterno = new User("Esterno", "Neri", "esterno@test.com", Ruolo.UTENTE_AUTENTICATO);

        userRepo.save(organizzatore);
        userRepo.save(mentore);
        userRepo.save(leader);
        userRepo.save(membro);
        userRepo.save(esterno);

        // Hackathon
        List<User> mentori = new ArrayList<>();
        mentori.add(mentore);
        hackathon = new Hackathon("Support Hack", "Rules", LocalDateTime.now(), LocalDateTime.now(),
                LocalDateTime.now(), LocalDateTime.now(), "Online", 100, 2, organizzatore, null, mentori,
                StatoHackathon.IN_CORSO);
        hackathonRepo.save(hackathon);

        // Team
        team = new Team("Support Team", leader);
        team.getMembri().add(membro);
        teamRepo.save(team);

        // Partecipazione
        Partecipazione p = new Partecipazione(team, hackathon);
        partecipazioneRepo.save(p);
    }

    private static void testRichiestaSuccesso() {
        System.out.println(">> TEST 1: Creazione Richiesta con Successo (Membro)");
        CreaRichiestaSupportoRequest request = new CreaRichiestaSupportoRequest(
                hackathon.getId(),
                team.getId(),
                membro.getId(),
                "Non riesco a configurare Docker.");

        Object response = controller.creaRichiestaSupporto(request);
        System.out.println("Response: " + response);

        if (response.toString().contains("Successo 200")) {
            System.out.println("   [OK] Richiesta creata.");
        } else {
            System.out.println("   [FAIL] Errore inatteso.");
        }
    }

    private static void testErroreNonMembro() {
        System.out.println("\n>> TEST 2: Errore Utente Non Membro del Team");
        CreaRichiestaSupportoRequest request = new CreaRichiestaSupportoRequest(
                hackathon.getId(),
                team.getId(),
                esterno.getId(),
                "Vorrei aiuto ma non sono del team.");

        Object response = controller.creaRichiestaSupporto(request);
        System.out.println("Response: " + response);

        if (response.toString().contains("Errore 500") && response.toString().contains("non appartiene al team")) {
            System.out.println("   [OK] Errore catturato correttamente.");
        } else {
            System.out.println("   [FAIL] Il sistema non ha bloccato l'esterno.");
        }
    }

    private static void testErroreNonPartecipante() {
        System.out.println("\n>> TEST 3: Errore Team Non Partecipante");

        // Nuovo team non iscritto
        Team team2 = new Team("No Hack Team", leader);
        teamRepo.save(team2);

        CreaRichiestaSupportoRequest request = new CreaRichiestaSupportoRequest(
                hackathon.getId(),
                team2.getId(),
                leader.getId(),
                "Aiuto su nulla.");

        Object response = controller.creaRichiestaSupporto(request);
        System.out.println("Response: " + response);

        if (response.toString().contains("Errore 500")
                && response.toString().contains("Nessuna partecipazione trovata")) {
            System.out.println("   [OK] Errore catturato correttamente.");
        } else {
            System.out.println("   [FAIL] Il sistema ha accettato richiesta da team non iscritto.");
        }
    }
}
