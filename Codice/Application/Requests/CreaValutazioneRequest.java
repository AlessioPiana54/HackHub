package Application.Requests;

public class CreaValutazioneRequest {
    private String idSottomissione;
    private String idGiudice;
    private double voto;
    private String giudizio;

    public CreaValutazioneRequest(String idSottomissione, String idGiudice, double voto, String giudizio) {
        this.idSottomissione = idSottomissione;
        this.idGiudice = idGiudice;
        this.voto = voto;
        this.giudizio = giudizio;
    }

    public String getIdSottomissione() {
        return idSottomissione;
    }

    public String getIdGiudice() {
        return idGiudice;
    }

    public double getVoto() {
        return voto;
    }

    public String getGiudizio() {
        return giudizio;
    }
}
