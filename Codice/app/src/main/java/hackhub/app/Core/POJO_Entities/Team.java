package hackhub.app.Core.POJO_Entities;

import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;

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

    public Team() {
    }

    public Team(String nomeTeam, User leaderSquadra) {
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
