package hackhub.app.Core.POJO_Entities;

import jakarta.persistence.*;

/**
 * Rappresenta la valutazione di una sottomissione da parte di un giudice.
 * <p>
 * Contiene il voto numerico e un giudizio testuale.
 * </p>
 */
@Entity
@Table(name = "valutazioni", indexes = {
    @Index(name = "idx_valutazioni_giudice", columnList = "giudice_id")
})
public class Valutazione {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    @JoinColumn(name = "sottomissione_id", nullable = false, unique = true)
    private Sottomissione sottomissione;

    @ManyToOne
    @JoinColumn(name = "giudice_id", nullable = false)
    private User giudice;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String giudizio;

    @Column(nullable = false, columnDefinition = "NUMERIC(4,2) CHECK (voto >= 0 AND voto <= 10)")
    private double voto;

    public Valutazione() {
    }

    public Valutazione(User giudice, String giudizio, double voto) {
        this.giudice = giudice;
        this.giudizio = giudizio;
        this.voto = voto;
    }

    public void setSottomissione(Sottomissione sottomissione) {
        this.sottomissione = sottomissione;
    }

    public Sottomissione getSottomissione() {
        return sottomissione;
    }

    public String getId() {
        return id;
    }

    public User getGiudice() {
        return giudice;
    }

    public String getGiudizio() {
        return giudizio;
    }

    public double getVoto() {
        return voto;
    }
}
