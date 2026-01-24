package hackhub.app.Application.Requests;

public class CreaInvitoRequest {
    private String teamId;
    private String emailDestinatario;
    private String userMittenteId;

    public CreaInvitoRequest(String teamId, String emailDestinatario, String userMittenteId) {
        this.teamId = teamId;
        this.emailDestinatario = emailDestinatario;
        this.userMittenteId = userMittenteId;
    }

    public String getTeamId() {
        return teamId;
    }

    public String getEmailDestinatario() {
        return emailDestinatario;
    }

    public String getUserMittenteId() {
        return userMittenteId;
    }
}
