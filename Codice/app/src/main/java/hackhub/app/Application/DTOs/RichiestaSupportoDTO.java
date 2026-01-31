package hackhub.app.Application.DTOs;

import java.time.LocalDateTime;

public class RichiestaSupportoDTO {
    private String id;
    private String hackathonId;
    private String hackathonNome;
    private String teamId;
    private String teamNome;
    private String descrizione;
    private LocalDateTime dataRichiesta;
    private String linkCall;
    private LocalDateTime dataCall;

    public RichiestaSupportoDTO(String id, String hackathonId, String hackathonNome, String teamId, String teamNome,
            String descrizione, LocalDateTime dataRichiesta, String linkCall, LocalDateTime dataCall) {
        this.id = id;
        this.hackathonId = hackathonId;
        this.hackathonNome = hackathonNome;
        this.teamId = teamId;
        this.teamNome = teamNome;
        this.descrizione = descrizione;
        this.dataRichiesta = dataRichiesta;
        this.linkCall = linkCall;
        this.dataCall = dataCall;
    }

    public String getId() {
        return id;
    }

    public String getHackathonId() {
        return hackathonId;
    }

    public String getHackathonNome() {
        return hackathonNome;
    }

    public String getTeamId() {
        return teamId;
    }

    public String getTeamNome() {
        return teamNome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public LocalDateTime getDataRichiesta() {
        return dataRichiesta;
    }

    public String getLinkCall() {
        return linkCall;
    }

    public LocalDateTime getDataCall() {
        return dataCall;
    }
}
