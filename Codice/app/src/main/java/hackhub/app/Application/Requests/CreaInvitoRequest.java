package hackhub.app.Application.Requests;

public class CreaInvitoRequest {
    private String teamId;
    private String emailDestinatario;

    public CreaInvitoRequest(String teamId, String emailDestinatario) {
        this.teamId = teamId;
        this.emailDestinatario = emailDestinatario;
    }

    public String getTeamId() {
        return teamId;
    }

    public String getEmailDestinatario() {
        return emailDestinatario;
    }

}
