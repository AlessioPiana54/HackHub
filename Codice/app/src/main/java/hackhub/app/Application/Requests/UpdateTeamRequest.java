package hackhub.app.Application.Requests;

public class UpdateTeamRequest {
    private String nomeTeam;

    public UpdateTeamRequest() {}

    public UpdateTeamRequest(String nomeTeam) {
        this.nomeTeam = nomeTeam;
    }

    public String getNomeTeam() {
        return nomeTeam;
    }

    public void setNomeTeam(String nomeTeam) {
        this.nomeTeam = nomeTeam;
    }
}
