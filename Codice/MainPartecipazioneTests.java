import Application.Services.TeamService;
import Application.Requests.IscriviTeamRequest;
import Core.Enums.Ruolo;
import Core.Enums.StatoHackathon;
import Core.POJO_Entities.Hackathon;
import Core.POJO_Entities.Partecipazione;
import Core.POJO_Entities.Team;
import Core.POJO_Entities.User;
import Infrastructure.Databases.InMemoryDatabase;
import Infrastructure.Repositories.HackathonRepository;
import Infrastructure.Repositories.PartecipazioneRepository;
import Infrastructure.Repositories.TeamRepository;
import Infrastructure.Repositories.UserRepository;
import java.time.LocalDateTime;

public class MainPartecipazioneTests {

    public static void main(String[] args) {
        setup();
        System.out.println("--- INIZIO TEST PARTECIPAZIONE ---\n");

        testIscrizioneSuccesso();
        testErroreHackathonNonInIscrizione();
        testErroreGiaIscritto();
        testErroreNonLeader(); // Nuovo test

        System.out.println("\n--- FINE TEST ---");
    }

    static TeamService teamService;
    static TeamRepository teamRepo;
    static HackathonRepository hackathonRepo;
    static PartecipazioneRepository partecipazioneRepo;
    static UserRepository userRepo;

    static Team team1;
    static Hackathon hackathonAperto;
    static Hackathon hackathonChiuso;

    private static void setup() {
        InMemoryDatabase db = InMemoryDatabase.getInstance();
        teamRepo = new TeamRepository(db);
        hackathonRepo = new HackathonRepository(db);
        partecipazioneRepo = new PartecipazioneRepository(db);
        userRepo = new UserRepository(db);

        teamService = new TeamService(teamRepo, userRepo, hackathonRepo, partecipazioneRepo);

        // User & Team
        User leader = new User("Leader", "P", "l@p.com", Ruolo.LEADER_TEAM);
        userRepo.save(leader);
        team1 = new Team("Team Alpha", leader);
        teamRepo.save(team1);

        // Membro Semplice (per il test errore)
        User membro = new User("Membro", "S", "m@s.com", Ruolo.MEMBRO_TEAM);
        userRepo.save(membro);
        team1.getMembri().add(membro); // Aggiungiamo il membro al team

        // Hackathons
        hackathonAperto = new Hackathon("Open Hack", "...", LocalDateTime.now(), LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(6), LocalDateTime.now().plusDays(10), "Online", 1000, 5,
                leader, leader, null, StatoHackathon.IN_ISCRIZIONE);
        hackathonRepo.save(hackathonAperto);

        hackathonChiuso = new Hackathon("Closed Hack", "...", LocalDateTime.now().minusDays(10),
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(4), LocalDateTime.now().minusDays(1), "Online", 1000, 5,
                leader, leader, null, StatoHackathon.CONCLUSO);
        hackathonRepo.save(hackathonChiuso);
    }

    private static void testIscrizioneSuccesso() {
        System.out.println(">> Test 1: Iscrizione Successo");
        try {
            // Passiamo l'ID del leader (team1.getLeaderSquadra().getId())
            Partecipazione p = teamService.iscriviTeam(
                    new IscriviTeamRequest(team1.getId(), hackathonAperto.getId(), team1.getLeaderSquadra().getId()));
            if (p != null && p.getTeam().getId().equals(team1.getId())) {
                System.out.println("   [OK] Iscrizione avvenuta.");
            } else {
                System.out.println("   [FAIL] Partecipazione null o errata.");
            }
        } catch (Exception e) {
            System.out.println("   [FAIL] Eccezione imprevista: " + e.getMessage());
        }
    }

    private static void testErroreHackathonNonInIscrizione() {
        System.out.println("\n>> Test 2: Errore Hackathon Non in Iscrizione");
        // Creiamo Hackathon In Corso (non in iscrizione)
        Hackathon inCorso = new Hackathon("Running Hack", "...", LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now(), LocalDateTime.now().plusDays(2), "Online", 100, 3, null, null, null,
                StatoHackathon.IN_CORSO);
        hackathonRepo.save(inCorso);

        try {
            teamService.iscriviTeam(
                    new IscriviTeamRequest(team1.getId(), inCorso.getId(), team1.getLeaderSquadra().getId()));
            System.out.println("   [FAIL] Doveva fallire.");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("non sono aperte")) {
                System.out.println("   [OK] Errore rilevato: " + e.getMessage());
            } else {
                System.out.println("   [FAIL] Messaggio errore diverso: " + e.getMessage());
            }
        }
    }

    private static void testErroreGiaIscritto() {
        System.out.println("\n>> Test 3: Errore Team Già Iscritto");
        // Team1 è già iscritto a hackathonAperto dal Test 1

        // Proviamo a iscriverlo ad un altro hackathon aperto
        Hackathon altroHack = new Hackathon("Altro Hack", "...", LocalDateTime.now(), LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(6), LocalDateTime.now().plusDays(10), "Online", 1000, 5,
                null, null, null, StatoHackathon.IN_ISCRIZIONE);
        hackathonRepo.save(altroHack);

        try {
            teamService.iscriviTeam(
                    new IscriviTeamRequest(team1.getId(), altroHack.getId(), team1.getLeaderSquadra().getId()));
            System.out.println("   [FAIL] Doveva fallire per regola 'un solo hackathon'.");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("già iscritto")) {
                System.out.println("   [OK] Errore rilevato: " + e.getMessage());
            } else {
                System.out.println("   [FAIL] Messaggio errore diverso: " + e.getMessage());
            }
        }
    }

    private static void testErroreNonLeader() {
        System.out.println("\n>> Test 4: Errore Utente Non Leader");

        // Nuova Iscrizione su nuovo Hackathon (altrimenti scatta "già iscritto")
        Hackathon hackNew = new Hackathon("New Hack", "...", LocalDateTime.now(), LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(6), LocalDateTime.now().plusDays(10), "Online", 1000, 5,
                null, null, null, StatoHackathon.IN_ISCRIZIONE);
        hackathonRepo.save(hackNew);

        // Recuperiamo un membro che non sia leader (aggiunto nel setup)
        // team1.getMembri().get(1) dovrebbe essere il membro aggiunto dopo il leader
        // Ma per sicurezza cerchiamo l'utente che NON è il leader
        User nonLeader = team1.getMembri().stream()
                .filter(u -> !u.getId().equals(team1.getLeaderSquadra().getId()))
                .findFirst()
                .orElse(null);

        if (nonLeader == null) {
            System.out.println("   [SKIP] Impossibile trovare un non-leader per il test.");
            return;
        }

        try {
            teamService.iscriviTeam(new IscriviTeamRequest(team1.getId(), hackNew.getId(), nonLeader.getId()));
            System.out.println("   [FAIL] Doveva fallire perché l'utente non è il leader.");
        } catch (SecurityException e) {
            if (e.getMessage().contains("Solo il Leader")) {
                System.out.println("   [OK] Errore autorizzazione rilevato: " + e.getMessage());
            } else {
                System.out.println("   [FAIL] Messaggio errore diverso: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("   [FAIL] Eccezione sbagliata: " + e.getClass().getSimpleName());
        }
    }
}
