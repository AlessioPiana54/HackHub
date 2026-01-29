package hackhub.app.Application.Requests;

public class IscriviTeamRequest {
    private String idTeam;
    private String idHackathon;

    public IscriviTeamRequest(String idTeam, String idHackathon) {
        this.idTeam = idTeam;
        this.idHackathon = idHackathon;
    }

    public String getIdTeam() {
        return idTeam;
    }

    public String getIdHackathon() {
        return idHackathon;
    }

}
