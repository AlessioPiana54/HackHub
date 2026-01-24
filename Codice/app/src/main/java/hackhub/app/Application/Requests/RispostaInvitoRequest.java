package hackhub.app.Application.Requests;

public class RispostaInvitoRequest {
    private String invitoId;
    private String userId; // Utente che risponde
    private Boolean accettato;

    public RispostaInvitoRequest(String invitoId, String userId, Boolean accettato) {
        this.invitoId = invitoId;
        this.userId = userId;
        this.accettato = accettato;
    }

    public String getInvitoId() {
        return invitoId;
    }

    public String getUserId() {
        return userId;
    }

    public Boolean isAccettato() {
        return accettato;
    }
}
