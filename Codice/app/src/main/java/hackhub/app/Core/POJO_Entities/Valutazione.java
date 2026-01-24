package hackhub.app.Core.POJO_Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "valutazioni")
public class Valutazione {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    @JoinColumn(name = "sottomissione_id")
    private Sottomissione sottomissione;

    @ManyToOne
    @JoinColumn(name = "giudice_id")
    private User giudice;

    private String giudizio;
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
