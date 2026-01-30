package hackhub.app.Application.Requests;

public class ModificaSottomissioneRequest extends InviaSottomissioneRequest {
    private String idSottomissione;

    public ModificaSottomissioneRequest(String idSottomissione, String idHackathon, String idTeam, String linkProgetto,
            String descrizione) {
        super(idHackathon, idTeam, linkProgetto, descrizione);
        this.idSottomissione = idSottomissione;
    }

    public String getIdSottomissione() {
        return idSottomissione;
    }
}
