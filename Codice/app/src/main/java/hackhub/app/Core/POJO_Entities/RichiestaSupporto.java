package hackhub.app.Core.POJO_Entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "richieste_supporto")
public class RichiestaSupporto {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "partecipazione_id")
    private Partecipazione partecipazione;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "hackathon_id")
    private Hackathon hackathon;

    @ManyToOne
    @JoinColumn(name = "richiedente_id")
    private User richiedente;

    private String descrizione;
    private LocalDateTime dataRichiesta;

    public RichiestaSupporto() {
    }

    public RichiestaSupporto(Partecipazione partecipazione, User richiedente, String descrizione) {
        this.partecipazione = partecipazione;
        this.richiedente = richiedente;
        this.descrizione = descrizione;
        this.dataRichiesta = LocalDateTime.now();
        this.team = partecipazione.getTeam();
        this.hackathon = partecipazione.getHackathon();
    }

    public String getId() {
        return id;
    }

    public Partecipazione getPartecipazione() {
        return partecipazione;
    }

    public User getRichiedente() {
        return richiedente;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public LocalDateTime getDataRichiesta() {
        return dataRichiesta;
    }
}
