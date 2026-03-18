package hackhub.app.Application.Requests;

public class ModificaSottomissioneRequest extends InviaSottomissioneRequest {

    public ModificaSottomissioneRequest(String idHackathon, String idTeam, String linkProgetto,
            String descrizione) {
        super(idHackathon, idTeam, linkProgetto, descrizione);
    }
}
