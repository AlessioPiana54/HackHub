package hackhub.app.Application.Requests;

import java.time.LocalDateTime;

public class ProponiCallRequest {
    private String richiestaId;
    private String linkCall;
    private LocalDateTime dataCall;

    public ProponiCallRequest() {
    }

    public ProponiCallRequest(String richiestaId, String linkCall, LocalDateTime dataCall) {
        this.richiestaId = richiestaId;
        this.linkCall = linkCall;
        this.dataCall = dataCall;
    }

    public String getRichiestaId() {
        return richiestaId;
    }

    public String getLinkCall() {
        return linkCall;
    }

    public LocalDateTime getDataCall() {
        return dataCall;
    }
}
