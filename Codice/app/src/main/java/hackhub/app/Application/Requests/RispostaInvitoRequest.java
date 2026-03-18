package hackhub.app.Application.Requests;

public class RispostaInvitoRequest {
    private Boolean accettato;

    public RispostaInvitoRequest() {}

    public RispostaInvitoRequest(Boolean accettato) {
        this.accettato = accettato;
    }

    public Boolean getAccettato() {
        return accettato;
    }
}
