import Application.IRepositories.*;
import Application.Requests.CreaInvitoRequest;
import Application.Requests.RispostaInvitoRequest;
import Application.Services.InvitoService;
import Core.Enums.Ruolo;
import Core.Enums.StatoHackathon;
import Core.POJO_Entities.*;
import Infrastructure.Databases.InMemoryDatabase;
import Infrastructure.Repositories.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class MainInvitoTests {
    public static void main(String[] args) {
        System.out.println("--- INIZIO TEST INVITO TEAM ---");

        // 1. Setup Environment
        InMemoryDatabase db = InMemoryDatabase.getInstance();
        
        // Repositories
        IUserRepository userRepository = new UserRepository(db);
        ITeamRepository teamRepository = new TeamRepository(db);
        IHackathonRepository hackathonRepository = new HackathonRepository(db);
        IPartecipazioneRepository partecipazioneRepository = new PartecipazioneRepository(db);
        IInvitoRepository invitoRepository = new InvitoRepository(db);

        // Service
        InvitoService invitoService = new InvitoService(invitoRepository, teamRepository, userRepository, partecipazioneRepository);

        // 2. Setup Data
        System.out.println("\n[SETUP] Creating Users and Team...");
        User leader = new User("Leader", "Team", "leader@test.com", Ruolo.UTENTE_SENZA_TEAM);
        User member = new User("Member", "Team", "member@test.com", Ruolo.UTENTE_SENZA_TEAM);
        User target = new User("Target", "User", "target@test.com", Ruolo.UTENTE_SENZA_TEAM);
        User target2 = new User("Target2", "User", "target2@test.com", Ruolo.UTENTE_SENZA_TEAM);
        User target3 = new User("Target3", "User", "target3@test.com", Ruolo.UTENTE_SENZA_TEAM);

        userRepository.save(leader);
        userRepository.save(member);
        userRepository.save(target);
        userRepository.save(target2);
        userRepository.save(target3);

        Team team = new Team("Alpha Team", leader);
        // Leader role updated via Constructor or Logic, but for test setup we can re-create or assume service does it. 
        // Here we simulate the state AFTER team creation, so leader should already appear as leader.
        // Re-creating leader object with correct role for initial state
        leader = new User(leader.getId(), leader.getNome(), leader.getCognome(), leader.getEmail(), Ruolo.LEADER_TEAM);
        userRepository.edit(leader);
        
        // Updating team leader reference to the new object (though technically ID ref is enough for repos)
        // team = new Team("Alpha Team", leader); // Re-creating team with updated leader if needed, but entity holds ref.
        
        User memberUpdated = new User(member.getId(), member.getNome(), member.getCognome(), member.getEmail(), Ruolo.MEMBRO_TEAM);
        userRepository.edit(memberUpdated);
        team.getMembri().add(memberUpdated);
        
        teamRepository.save(team);

        System.out.println("Team created: " + team.getNomeTeam() + " with Leader: " + team.getLeaderSquadra().getNome());

        // 3. Test: Send Invite
        System.out.println("\n[TEST 1] Send Invite...");
        try {
            CreaInvitoRequest req = new CreaInvitoRequest(team.getId(), target.getId(), leader.getId());
            Invito invito = invitoService.inviaInvito(req);
            System.out.println("Invito inviato con successo ID: " + invito.getId());
            
            if(team.getInvitiInSospeso().contains(invito)) {
                System.out.println("SUCCESS: Invito presente nel team.");
            } else {
                System.out.println("FAIL: Invito NON presente nel team.");
            }
        } catch (Exception e) {
            System.out.println("FAIL: Errore invio invito: " + e.getMessage());
        }

        // 4. Test: Accept Invite
        System.out.println("\n[TEST 2] Accept Invite...");
        try {
            Invito invito = invitoRepository.findByDestinatario(target).get(0);
            RispostaInvitoRequest req = new RispostaInvitoRequest(invito.getId(), target.getId(), true);
            invitoService.gestisciRisposta(req);
            
            User updatedTarget = userRepository.findById(target.getId());
            Team updatedTeam = teamRepository.findById(team.getId());
            
            if (updatedTarget.getRuolo() == Ruolo.MEMBRO_TEAM && updatedTeam.getMembri().contains(updatedTarget)) {
                System.out.println("SUCCESS: Utente entrato nel team.");
            } else {
                System.out.println("FAIL: Utente NON entrato nel team o ruolo errato.");
            }
            
            if (invitoRepository.findById(invito.getId()) == null) {
                System.out.println("SUCCESS: Invito eliminato.");
            } else {
                System.out.println("FAIL: Invito ancora presente.");
            }

        } catch (Exception e) {
            System.out.println("FAIL: Errore accettazione invito: " + e.getMessage());
            e.printStackTrace();
        }

        // 5. Test: Reject Invite
        System.out.println("\n[TEST 3] Reject Invite...");
        try {
            // Setup invite for target3
            CreaInvitoRequest reqInv = new CreaInvitoRequest(team.getId(), target3.getId(), leader.getId());
            Invito invito3 = invitoService.inviaInvito(reqInv);
            
            RispostaInvitoRequest reqRif = new RispostaInvitoRequest(invito3.getId(), target3.getId(), false);
            invitoService.gestisciRisposta(reqRif);
            
            User updatedTarget3 = userRepository.findById(target3.getId());
            if (updatedTarget3.getRuolo() == Ruolo.UTENTE_SENZA_TEAM) {
                 System.out.println("SUCCESS: Utente rimasto tale.");
            } else {
                 System.out.println("FAIL: Ruolo utente cambiato.");
            }
            
              if (invitoRepository.findById(invito3.getId()) == null) {
                System.out.println("SUCCESS: Invito eliminato.");
            } else {
                System.out.println("FAIL: Invito ancora presente.");
            }

        } catch (Exception e) {
             System.out.println("FAIL: Errore rifiuto invito: " + e.getMessage());
        }

        // 6. Test: Constraint Hackathon Active
        System.out.println("\n[TEST 4] Constraint Check (Active Hackathon)...");
        try {
            // Create Hackathon
            Hackathon hackathon = new Hackathon("Hackathon 2024", "Regole...", LocalDateTime.now(), LocalDateTime.now().plusDays(10), LocalDateTime.now().plusDays(20), LocalDateTime.now().plusDays(30), "Roma", 1000, 5, leader, leader, new ArrayList<>(), StatoHackathon.IN_CORSO);
            hackathonRepository.save(hackathon);
            
            Partecipazione p = new Partecipazione(team, hackathon);
            partecipazioneRepository.save(p);
            
            // Send invite to target2
            CreaInvitoRequest req = new CreaInvitoRequest(team.getId(), target2.getId(), leader.getId());
            Invito invito = invitoService.inviaInvito(req);
            
            System.out.println("Tentativo di accettare invito durante Hackathon IN_CORSO...");
            RispostaInvitoRequest reqAcc = new RispostaInvitoRequest(invito.getId(), target2.getId(), true);
            invitoService.gestisciRisposta(reqAcc);
            
            System.out.println("FAIL: Doveva lanciare eccezione!");
        } catch (IllegalStateException e) {
            System.out.println("SUCCESS: Eccezione corretta: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("FAIL: Eccezione inattesa: " + e.getMessage() + " Class: " + e.getClass().getName());
        }

        System.out.println("\n--- FINE TEST ---");
    }
}
