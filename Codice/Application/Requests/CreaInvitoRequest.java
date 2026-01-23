package Application.Requests;

public class CreaInvitoRequest {
    private String teamId;
    private String userDestinatarioId;
    private String userMittenteId;

    public CreaInvitoRequest(String teamId, String userDestinatarioId, String userMittenteId) {
        this.teamId = teamId;
        this.userDestinatarioId = userDestinatarioId;
        this.userMittenteId = userMittenteId;
    }

    public String getTeamId() {
        return teamId;
    }

    public String getUserDestinatarioId() {
        return userDestinatarioId;
    }

    public String getUserMittenteId() {
        return userMittenteId;
    }
}
