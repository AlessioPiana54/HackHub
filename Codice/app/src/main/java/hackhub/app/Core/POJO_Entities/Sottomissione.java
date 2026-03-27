package hackhub.app.Core.POJO_Entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Rappresenta il progetto sottomesso da un Team per un Hackathon.
 * <p>
 * Contiene il link al codice sorgente, la descrizione del progetto e i
 * riferimenti a chi ha inviato la sottomissione.
 * </p>
 */
@Entity
@Table(name = "sottomissioni",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_sottomissione_partecipazione", columnNames = {"partecipazione_id"})
    },
    indexes = {
        @Index(name = "idx_sottomissioni_mittente", columnList = "mittente_id")
    }
)
public class Sottomissione {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "partecipazione_id", nullable = false)
    private Partecipazione partecipazione;

    @ManyToOne
    @JoinColumn(name = "mittente_id", nullable = false)
    private User mittente; // Membro o Leader

    @Column(nullable = false)
    private String linkProgetto; // URL repository

    @Column(columnDefinition = "TEXT")
    private String descrizione;

    @Column(nullable = false)
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
