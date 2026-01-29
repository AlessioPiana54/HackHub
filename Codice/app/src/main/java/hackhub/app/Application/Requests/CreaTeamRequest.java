package hackhub.app.Application.Requests;

public class CreaTeamRequest {

    private String nomeTeam;

    public CreaTeamRequest(String nomeTeam) {
        this.nomeTeam = nomeTeam;
    }

    public String getNomeTeam() {
        return nomeTeam;
    }
}
