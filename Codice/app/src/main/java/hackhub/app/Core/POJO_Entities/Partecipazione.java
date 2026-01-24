package hackhub.app.Core.POJO_Entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "partecipazioni", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "team_id", "hackathon_id" })
})
public class Partecipazione {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "hackathon_id")
    private Hackathon hackathon;

    private LocalDateTime dataIscrizione;

    public Partecipazione() {
    }

    public Partecipazione(Team team, Hackathon hackathon) {
        this.team = team;
        this.hackathon = hackathon;
        this.dataIscrizione = LocalDateTime.now();
    }

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
