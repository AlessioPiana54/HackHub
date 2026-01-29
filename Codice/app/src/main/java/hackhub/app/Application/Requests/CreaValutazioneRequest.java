package hackhub.app.Application.Requests;

public class CreaValutazioneRequest {
    private String idSottomissione;
    private double voto;
    private String giudizio;

    public CreaValutazioneRequest(String idSottomissione, double voto, String giudizio) {
        this.idSottomissione = idSottomissione;
        this.voto = voto;
        this.giudizio = giudizio;
    }

    public String getIdSottomissione() {
        return idSottomissione;
    }

    public double getVoto() {
        return voto;
    }

    public String getGiudizio() {
        return giudizio;
    }
}
