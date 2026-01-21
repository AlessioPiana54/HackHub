package Application.Requests;

public class InviaSottomissioneRequest {
    private String idHackathon;
    private String idTeam;
    private String idUtente; // Chi fa la richiesta
    private String linkProgetto;
    private String descrizione;

    public InviaSottomissioneRequest(String idHackathon, String idTeam, String idUtente, String linkProgetto,
            String descrizione) {
        this.idHackathon = idHackathon;
        this.idTeam = idTeam;
        this.idUtente = idUtente;
        this.linkProgetto = linkProgetto;
        this.descrizione = descrizione;
    }

    public String getIdHackathon() {
        return idHackathon;
    }

    public String getIdTeam() {
        return idTeam;
    }

    public String getIdUtente() {
        return idUtente;
    }

    public String getLinkProgetto() {
        return linkProgetto;
    }

    public String getDescrizione() {
        return descrizione;
    }
}
