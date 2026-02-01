package hackhub.app.Core.POJO_Entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Rappresenta una segnalazione effettuata da un mentore riguardo un team.
 * <p>
 * Utilizzata per riportare problemi o comportamenti scorretti
 * all'organizzatore.
 * </p>
 */
@Entity
@Table(name = "segnalazioni")
public class Segnalazione {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "partecipazione_id")
    private Partecipazione partecipazione;

    @ManyToOne
    @JoinColumn(name = "mentore_id")
    private User mentore;

    private String descrizione;
    private LocalDateTime dataSegnalazione;

    public Segnalazione() {
    }

    public Segnalazione(Partecipazione partecipazione, User mentore, String descrizione) {
        this.partecipazione = partecipazione;
        this.mentore = mentore;
        this.descrizione = descrizione;
        this.dataSegnalazione = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public Partecipazione getPartecipazione() {
        return partecipazione;
    }

    public User getMentore() {
        return mentore;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public LocalDateTime getDataSegnalazione() {
        return dataSegnalazione;
    }

    public Team getTeam() {
        return partecipazione.getTeam();
    }

    public Hackathon getHackathon() {
        return partecipazione.getHackathon();
    }
}
