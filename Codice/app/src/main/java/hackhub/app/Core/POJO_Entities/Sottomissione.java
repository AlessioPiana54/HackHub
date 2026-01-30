package hackhub.app.Core.POJO_Entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sottomissioni")
public class Sottomissione {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "partecipazione_id")
    private Partecipazione partecipazione;

    @ManyToOne
    @JoinColumn(name = "mittente_id")
    private User mittente; // Membro o Leader

    private String linkProgetto; // URL repository
    private String descrizione;
    private LocalDateTime dataSottomissione;

    public Sottomissione() {
    }

    public Sottomissione(Partecipazione partecipazione, User mittente, String linkProgetto, String descrizione) {
        this.partecipazione = partecipazione;
        this.mittente = mittente;
        this.linkProgetto = linkProgetto;
        this.descrizione = descrizione;
        this.dataSottomissione = LocalDateTime.now();
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

    public User getMittente() {
        return mittente;
    }

    public String getLinkProgetto() {
        return linkProgetto;
    }

    public void setLinkProgetto(String linkProgetto) {
        this.linkProgetto = linkProgetto;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public LocalDateTime getDataSottomissione() {
        return dataSottomissione;
    }
}
