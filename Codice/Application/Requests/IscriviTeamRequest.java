package Application.Requests;

public class IscriviTeamRequest {
    private String idTeam;
    private String idHackathon;

    private String idRichiedente;

    public IscriviTeamRequest(String idTeam, String idHackathon, String idRichiedente) {
        this.idTeam = idTeam;
        this.idHackathon = idHackathon;
        this.idRichiedente = idRichiedente;
    }

    public String getIdTeam() {
        return idTeam;
    }

    public String getIdHackathon() {
        return idHackathon;
    }

    public String getIdRichiedente() {
        return idRichiedente;
    }
}
