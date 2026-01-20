package Infrastructure.Databases;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Core.POJO_Entities.Hackathon;
import Core.POJO_Entities.Team;
import Core.POJO_Entities.User;

public class InMemoryDatabase {
    // Singleton pattern semplificato per accesso condiviso
    private static InMemoryDatabase instance;
    
    public static synchronized InMemoryDatabase getInstance() {
        if (instance == null) {
            instance = new InMemoryDatabase();
        }
        return instance;
    }

    private Map<String, Hackathon> hackathons = new HashMap<>();
    private Map<String, User> users = new HashMap<>();
    private Map<String, Team> teams = new HashMap<>();

    // --- USERS ---
    public void saveUser(User user) {
        users.put(user.getId(), user);
    }
    public User getUser(String id) {
        return users.get(id);
    }
    public User getUserByEmail(String email) {
        return users.values().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    // --- HACKATHONS ---
    public void saveHackathon(Hackathon h) {
        hackathons.put(h.getId(), h);
    }
    public void editHackathon(Hackathon h) {
        // Controllo di sicurezza: Aggiorno solo se esiste davvero
        if (hackathons.containsKey(h.getId())) {
            hackathons.put(h.getId(), h);
        } else {
            // Se proviamo a editare qualcosa che non c'è, lanciamo un errore
            throw new IllegalArgumentException("Impossibile modificare: Hackathon con ID " + h.getId() + " non trovato.");
        }
    }
    public Hackathon getHackathon(String id) {
        return hackathons.get(id);
    }
    public List<Hackathon> getAllHackathons() {
        return new ArrayList<>(hackathons.values());
    }
    public void deleteHackathon(String id) {
        hackathons.remove(id);
    }
}
