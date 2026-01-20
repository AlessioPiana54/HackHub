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

public class Main {
    public static void main(String[] args) {
        System.out.println("--- INIZIO TEST SISTEMA HACKATHON ---\n");

        // 1. SETUP INFRASTRUTTURA E DIPENDENZE
        // Inizializziamo il database in memoria e i repository
        InMemoryDatabase db = InMemoryDatabase.getInstance();
        UserRepository userRepo = new UserRepository(db);
        HackathonRepository hackathonRepo = new HackathonRepository(db);

        // Inizializziamo il Service e il Validator
        CreaHackathonValidator validator = new CreaHackathonValidator();
        HackathonService service = new HackathonService(hackathonRepo, userRepo);

        // Inizializziamo il Controller iniettando il Service e il Validator
        HackathonController controller = new HackathonController(service, validator);


        // 2. PREPARAZIONE DATI (PREREQUISITI)
        // Il service fallirebbe se non trovasse gli utenti con i ruoli corretti nel DB.
        // Creiamo un Organizzatore e un Giudice.
        User organizzatore = new User("Mario", "Rossi", "mario@org.com", Ruolo.ORGANIZZATORE);
        User giudice = new User("Luigi", "Verdi", "luigi@judge.com", Ruolo.GIUDICE);

        // Salviamo gli utenti nel "DB"
        userRepo.save(organizzatore);
        userRepo.save(giudice);

        System.out.println(">> Utenti di test inseriti nel DB:");
        System.out.println("   Organizzatore (ID): " + organizzatore.getId());
        System.out.println("   Giudice (ID):       " + giudice.getId() + "\n");


        // 3. CREAZIONE DELLA RICHIESTA (Simulazione Input Utente)
        // Impostiamo date coerenti (Iscrizioni -> Chiusura -> Inizio -> Fine)
        LocalDateTime now = LocalDateTime.now();
        
        CreaHackathonRequest request = new CreaHackathonRequest(
            "Java Super Hackathon",                // Nome
            "Vietato usare codice generato...",    // Regolamento
            now.plusDays(2),                       // Inizio Iscrizioni
            now.plusDays(10),                      // Scadenza Iscrizioni
            now.plusDays(15),                      // Data Inizio Evento
            now.plusDays(17),                      // Data Fine Evento
            "Milano, Campus",                      // Luogo
            5000.00,                               // Premio
            4,                                     // Max Team Size
            organizzatore.getId(),                 // ID Organizzatore (deve esistere)
            giudice.getId(),                       // ID Giudice (deve esistere)
            new ArrayList<>()                      // Lista Mentori (vuota per semplicità)
        );


        // 4. ESECUZIONE DEL TEST
        System.out.println(">> Invio richiesta creazione Hackathon al Controller...");
        
        // Chiamiamo il metodo del controller
        Object risultato = controller.creaHackathon(request);


        // 5. VERIFICA DEL RISULTATO
        System.out.println("\n>> RISULTATO OPERAZIONE:");
        System.out.println(risultato.toString());

        // Verifica extra: Stampiamo quanti hackathon ci sono ora nel DB
        System.out.println("\n>> Verifica persistenza:");
        System.out.println("   Hackathon nel DB: " + hackathonRepo.findAll().size());
        
        if (!hackathonRepo.findAll().isEmpty()) {
             System.out.println("   Dettagli: " + hackathonRepo.findAll().get(0).getNome() + 
                                " [Stato: " + hackathonRepo.findAll().get(0).getStato() + "]");
        }
    }
}
