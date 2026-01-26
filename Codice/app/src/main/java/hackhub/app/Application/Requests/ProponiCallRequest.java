package hackhub.app.Application.Requests;

import java.time.LocalDateTime;

public class ProponiCallRequest {
    private String richiestaId;
    private String mentorId;
    private String linkCall;
    private LocalDateTime dataCall;

    public ProponiCallRequest() {
    }

    public ProponiCallRequest(String richiestaId, String mentorId, String linkCall, LocalDateTime dataCall) {
        this.richiestaId = richiestaId;
        this.mentorId = mentorId;
        this.linkCall = linkCall;
        this.dataCall = dataCall;
    }

    public String getRichiestaId() {
        return richiestaId;
    }

    public String getMentorId() {
        return mentorId;
    }

    public String getLinkCall() {
        return linkCall;
    }

    public LocalDateTime getDataCall() {
        return dataCall;
    }
}
