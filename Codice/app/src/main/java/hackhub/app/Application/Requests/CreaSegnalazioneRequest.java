package hackhub.app.Application.Requests;

public class CreaSegnalazioneRequest {
    private String idHackathon;
    private String idTeam;
    private String descrizione;

    public CreaSegnalazioneRequest(String idHackathon, String idTeam, String descrizione) {
        this.idHackathon = idHackathon;
        this.idTeam = idTeam;
        this.descrizione = descrizione;
    }

    public String getIdHackathon() {
        return idHackathon;
    }

    public String getIdTeam() {
        return idTeam;
    }

    public String getDescrizione() {
        return descrizione;
    }
}
