package hackhub.app.Application.Requests;

public class CreaRichiestaSupportoRequest {
    private String hackathonId;
    private String teamId;
    private String descrizione;

    public CreaRichiestaSupportoRequest(String hackathonId, String teamId, String descrizione) {
        this.hackathonId = hackathonId;
        this.teamId = teamId;
        this.descrizione = descrizione;
    }

    public String getHackathonId() {
        return hackathonId;
    }

    public String getTeamId() {
        return teamId;
    }

    public String getDescrizione() {
        return descrizione;
    }
}
