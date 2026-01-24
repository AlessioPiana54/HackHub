package Infrastructure.Databases;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Core.POJO_Entities.Hackathon;
import Core.POJO_Entities.Team;
import Core.POJO_Entities.User;
import Core.POJO_Entities.Sottomissione;
import Core.POJO_Entities.Segnalazione;
import Core.POJO_Entities.RichiestaSupporto;

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
    private Map<String, Sottomissione> sottomissioni = new HashMap<>();
    private Map<String, Core.POJO_Entities.Partecipazione> partecipazioni = new HashMap<>();
    private Map<String, Core.POJO_Entities.Invito> inviti = new HashMap<>();
    private Map<String, Segnalazione> segnalazioni = new HashMap<>();

    // --- INVITI ---
    public void saveInvito(Core.POJO_Entities.Invito invito) {
        inviti.put(invito.getId(), invito);
    }

    public Core.POJO_Entities.Invito getInvito(String id) {
        return inviti.get(id);
    }

    public void deleteInvito(String id) {
        inviti.remove(id);
    }

    public List<Core.POJO_Entities.Invito> getAllInviti() {
        return new ArrayList<>(inviti.values());
    }

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

    public void editUser(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        } else {
            throw new IllegalArgumentException(
                    "Impossibile modificare: Utente con ID " + user.getId() + " non trovato.");
        }
    }

    public void deleteUser(String id) {
        users.remove(id);
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
            throw new IllegalArgumentException(
                    "Impossibile modificare: Hackathon con ID " + h.getId() + " non trovato.");
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

    // --- TEAMS ---
    public void saveTeam(Team team) {
        teams.put(team.getId(), team);
    }

    public void editTeam(Team team) {
        // Controllo di sicurezza: Aggiorno solo se esiste davvero
        if (teams.containsKey(team.getId())) {
            teams.put(team.getId(), team);
        } else {
            throw new IllegalArgumentException("Impossibile modificare: Team con ID " + team.getId() + " non trovato.");
        }
    }

    public Team getTeam(String id) {
        return teams.get(id);
    }

    public List<Team> getAllTeams() {
        return new ArrayList<>(teams.values());
    }

    public void deleteTeam(String id) {
        teams.remove(id);
    }

    // --- SOTTOMISSIONI ---
    public void salvaSottomissione(Sottomissione s) {
        sottomissioni.put(s.getId(), s);
    }

    public void editSottomissione(Sottomissione s) {
        if (sottomissioni.containsKey(s.getId())) {
            sottomissioni.put(s.getId(), s);
        } else {
            throw new IllegalArgumentException("Sottomissione non trovata: " + s.getId());
        }
    }

    public Sottomissione getSottomissione(String id) {
        return sottomissioni.get(id);
    }

    public List<Sottomissione> getAllSottomissioni() {
        return new ArrayList<>(sottomissioni.values());
    }

    public void deleteSottomissione(String id) {
        sottomissioni.remove(id);
    }

    public List<Sottomissione> getSottomissioniByHackathon(String idHackathon) {
        List<Sottomissione> result = new ArrayList<>();
        for (Sottomissione s : sottomissioni.values()) {
            if (s.getPartecipazione().getHackathon().getId().equals(idHackathon)) {
                result.add(s);
            }
        }
        return result;
    }

    public List<Sottomissione> getSottomissioniByTeam(String idTeam) {
        List<Sottomissione> result = new ArrayList<>();
        for (Sottomissione s : sottomissioni.values()) {
            if (s.getPartecipazione().getTeam().getId().equals(idTeam)) {
                result.add(s);
            }
        }
        return result;
    }

    // --- PARTECIPAZIONI ---
    public void savePartecipazione(Core.POJO_Entities.Partecipazione p) {
        partecipazioni.put(p.getId(), p);
    }

    public Core.POJO_Entities.Partecipazione getPartecipazione(String id) {
        return partecipazioni.get(id);
    }

    public List<Core.POJO_Entities.Partecipazione> getAllPartecipazioni() {
        return new ArrayList<>(partecipazioni.values());
    }

    public List<Core.POJO_Entities.Partecipazione> getPartecipazioniByTeam(String idTeam) {
        List<Core.POJO_Entities.Partecipazione> result = new ArrayList<>();
        for (Core.POJO_Entities.Partecipazione p : partecipazioni.values()) {
            if (p.getTeam().getId().equals(idTeam)) {
                result.add(p);
            }
        }
        return result;
    }

    public List<Core.POJO_Entities.Partecipazione> getPartecipazioniByHackathon(String idHackathon) {
        List<Core.POJO_Entities.Partecipazione> result = new ArrayList<>();
        for (Core.POJO_Entities.Partecipazione p : partecipazioni.values()) {
            if (p.getHackathon().getId().equals(idHackathon)) {
                result.add(p);
            }
        }
        return result;
    }

    public void deletePartecipazione(String id) {
        partecipazioni.remove(id);
    }

    // --- SEGNALAZIONI ---
    public void saveSegnalazione(Segnalazione s) {
        segnalazioni.put(s.getId(), s);
    }

    public Segnalazione getSegnalazione(String id) {
        return segnalazioni.get(id);
    }

    public List<Segnalazione> getAllSegnalazioni() {
        return new ArrayList<>(segnalazioni.values());
    }

    public List<Segnalazione> getSegnalazioniByHackathon(String idHackathon) {
        List<Segnalazione> result = new ArrayList<>();
        for (Segnalazione s : segnalazioni.values()) {
            if (s.getPartecipazione().getHackathon().getId().equals(idHackathon)) {
                result.add(s);
            }
        }
        return result;
    }

    // --- RICHIESTE SUPPORTO ---
    private Map<String, RichiestaSupporto> richiesteSupporto = new HashMap<>();

    public void saveRichiestaSupporto(RichiestaSupporto r) {
        richiesteSupporto.put(r.getId(), r);
    }

    public RichiestaSupporto getRichiestaSupporto(String id) {
        return richiesteSupporto.get(id);
    }

    public List<RichiestaSupporto> getAllRichiesteSupporto() {
        return new ArrayList<>(richiesteSupporto.values());
    }

    public List<RichiestaSupporto> getRichiesteSupportoByHackathon(String idHackathon) {
        List<RichiestaSupporto> result = new ArrayList<>();
        for (RichiestaSupporto r : richiesteSupporto.values()) {
            if (r.getPartecipazione().getHackathon().getId().equals(idHackathon)) {
                result.add(r);
            }
        }
        return result;
    }
}
