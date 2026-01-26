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
    @JoinColumn(name = "richiedente_id")
    private User richiedente;

    private String descrizione;
    private LocalDateTime dataRichiesta;

    private String linkCall;
    private LocalDateTime dataCall;

    public RichiestaSupporto() {
    }

    public RichiestaSupporto(Partecipazione partecipazione, User richiedente, String descrizione) {
        this.partecipazione = partecipazione;
        this.richiedente = richiedente;
        this.descrizione = descrizione;
        this.dataRichiesta = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public Hackathon getHackathon() {
        return partecipazione.getHackathon();
    }

    public Team getTeam() {
        return partecipazione.getTeam();
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

    public String getLinkCall() {
        return linkCall;
    }

    public void setLinkCall(String linkCall) {
        this.linkCall = linkCall;
    }

    public LocalDateTime getDataCall() {
        return dataCall;
    }

    public void setDataCall(LocalDateTime dataCall) {
        this.dataCall = dataCall;
    }
}
