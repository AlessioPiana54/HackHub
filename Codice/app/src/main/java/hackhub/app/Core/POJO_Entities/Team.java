package hackhub.app.Core.POJO_Entities;

import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

/**
 * Rappresenta un team di partecipanti.
 * <p>
 * Un team è composto da un leader e da membri. Partecipa agli Hackathon tramite
 * l'entità Partecipazione.
 * </p>
 */
@Entity
@Table(name = "teams")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String nomeTeam;

    @OneToOne
    @JoinColumn(name = "leader_id")
    private User leaderSquadra;

    @ManyToMany
    @JoinTable(name = "team_membri", joinColumns = @JoinColumn(name = "team_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> membri;

    private LocalDateTime dataCreazione;

    public Team() {
        this.dataCreazione = LocalDateTime.now();
    }

    public Team(String nomeTeam, User leaderSquadra) {
        this.nomeTeam = nomeTeam;
        this.leaderSquadra = leaderSquadra;
        this.membri = new ArrayList<>();
        this.membri.add(leaderSquadra);
        this.dataCreazione = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getNomeTeam() {
        return nomeTeam;
    }

    public void setNomeTeam(String nomeTeam) {
        this.nomeTeam = nomeTeam;
    }

    public User getLeaderSquadra() {
        return leaderSquadra;
    }

    public void setLeaderSquadra(User leaderSquadra) {
        this.leaderSquadra = leaderSquadra;
    }

    public List<User> getMembri() {
        return membri;
    }

    public LocalDateTime getDataCreazione() {
        return dataCreazione;
    }
}
