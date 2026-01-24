package hackhub.app.Core.POJO_Entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inviti")
public class Invito {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "destinatario_id")
    private User destinatario;

    @ManyToOne
    @JoinColumn(name = "mittente_id")
    private User mittente;

    private LocalDateTime dataInvito;

    public Invito() {
    }

    public Invito(Team team, User destinatario, User mittente) {
        this.team = team;
        this.destinatario = destinatario;
        this.mittente = mittente;
        this.dataInvito = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public Team getTeam() {
        return team;
    }

    public User getDestinatario() {
        return destinatario;
    }

    public User getMittente() {
        return mittente;
    }

    public LocalDateTime getDataInvito() {
        return dataInvito;
    }
}
