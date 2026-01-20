package Application.Requests;

public class CreaTeamRequest {

    private String nomeTeam;
    private String leaderSquadra;

    public CreaTeamRequest(String nomeTeam, String leaderSquadra) {
        this.nomeTeam = nomeTeam;
        this.leaderSquadra = leaderSquadra;
    }

    public String getNomeTeam() {
        return nomeTeam;
    }

    public String getLeaderSquadra() {
        return leaderSquadra;
    }
}
