package hackhub.app.Application.Requests;

public class InviaSottomissioneRequest {
    private String idHackathon;
    private String idTeam;
    private String linkProgetto;
    private String descrizione;

    public InviaSottomissioneRequest(String idHackathon, String idTeam, String linkProgetto,
            String descrizione) {
        this.idHackathon = idHackathon;
        this.idTeam = idTeam;
        this.linkProgetto = linkProgetto;
        this.descrizione = descrizione;
    }

    public String getIdHackathon() {
        return idHackathon;
    }

    public String getIdTeam() {
        return idTeam;
    }

    public String getLinkProgetto() {
        return linkProgetto;
    }

    public String getDescrizione() {
        return descrizione;
    }
}
