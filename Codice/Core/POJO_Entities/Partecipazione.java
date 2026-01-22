package Core.POJO_Entities;

import java.time.LocalDateTime;
import java.util.UUID;

public class Partecipazione {
    private String id;
    private Team team;
    private Hackathon hackathon;
    private LocalDateTime dataIscrizione;

    // Costruttore
    public Partecipazione(Team team, Hackathon hackathon) {
        this.id = UUID.randomUUID().toString();
        this.team = team;
        this.hackathon = hackathon;
        this.dataIscrizione = LocalDateTime.now();
    }

    // Getters
    public String getId() {
        return id;
    }

    public Team getTeam() {
        return team;
    }

    public Hackathon getHackathon() {
        return hackathon;
    }

    public LocalDateTime getDataIscrizione() {
        return dataIscrizione;
    }
}
