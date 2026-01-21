package Core.POJO_Entities;

public class Valutazione {
    private User giudice;
    private String giudizio;
    private double voto;

    public Valutazione(User giudice, String giudizio, double voto) {
        this.giudice = giudice;
        this.giudizio = giudizio;
        this.voto = voto;
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
