package Core.POJO_Entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Team {
    private String id;
    private String nomeTeam;
    private User leaderSquadra;
    private List<User> membri;

    // COSTRUTTORE SOLO A SCOPO DI TEST
    public Team(String nomeTeam, User leaderSquadra) {
        // Genera un ID Randomico
        this.id = UUID.randomUUID().toString();
        this.nomeTeam = nomeTeam;
        this.leaderSquadra = leaderSquadra;
        this.membri = new ArrayList<>();
        this.membri.add(leaderSquadra);
    }

    public String getId() {
        return id;
    }

    public String getNomeTeam() {
        return nomeTeam;
    }

    public User getLeaderSquadra() {
        return leaderSquadra;
    }

    public List<User> getMembri() {
        return membri;
    }
}
