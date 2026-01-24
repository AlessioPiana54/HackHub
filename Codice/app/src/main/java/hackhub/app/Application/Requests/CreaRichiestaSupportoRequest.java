package hackhub.app.Application.Requests;

public class CreaRichiestaSupportoRequest {
    private String hackathonId;
    private String teamId;
    private String richiedenteId;
    private String descrizione;

    public CreaRichiestaSupportoRequest(String hackathonId, String teamId, String richiedenteId, String descrizione) {
        this.hackathonId = hackathonId;
        this.teamId = teamId;
        this.richiedenteId = richiedenteId;
        this.descrizione = descrizione;
    }

    public String getHackathonId() {
        return hackathonId;
    }

    public String getTeamId() {
        return teamId;
    }

    public String getRichiedenteId() {
        return richiedenteId;
    }

    public String getDescrizione() {
        return descrizione;
    }
}
