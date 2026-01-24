package Application.Requests;

public class CreaSegnalazioneRequest {
    private String idHackathon;
    private String idTeam;
    private String idMentore;
    private String descrizione;

    public CreaSegnalazioneRequest(String idHackathon, String idTeam, String idMentore, String descrizione) {
        this.idHackathon = idHackathon;
        this.idTeam = idTeam;
        this.idMentore = idMentore;
        this.descrizione = descrizione;
    }

    public String getIdHackathon() {
        return idHackathon;
    }

    public String getIdTeam() {
        return idTeam;
    }

    public String getIdMentore() {
        return idMentore;
    }

    public String getDescrizione() {
        return descrizione;
    }
}
