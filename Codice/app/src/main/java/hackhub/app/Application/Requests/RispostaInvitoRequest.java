package hackhub.app.Application.Requests;

public class RispostaInvitoRequest {
    private String invitoId;
    private Boolean accettato;

    public RispostaInvitoRequest(String invitoId, Boolean accettato) {
        this.invitoId = invitoId;
        this.accettato = accettato;
    }

    public String getInvitoId() {
        return invitoId;
    }

    public Boolean isAccettato() {
        return accettato;
    }
}
