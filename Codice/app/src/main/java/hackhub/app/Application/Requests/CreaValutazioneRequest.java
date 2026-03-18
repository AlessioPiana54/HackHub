package hackhub.app.Application.Requests;

public class CreaValutazioneRequest {
    private double voto;
    private String giudizio;

    public CreaValutazioneRequest(double voto, String giudizio) {
        this.voto = voto;
        this.giudizio = giudizio;
    }

    public double getVoto() {
        return voto;
    }

    public String getGiudizio() {
        return giudizio;
    }
}
