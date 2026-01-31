package hackhub.app.Application.DTOs;

import java.time.LocalDateTime;

public class SegnalazioneDTO {
    private String id;
    private String hackathonId;
    private String hackathonNome;
    private String teamId;
    private String teamNome;
    private String mentoreId;
    private String mentoreNome;
    private String descrizione;
    private LocalDateTime dataSegnalazione;

    public SegnalazioneDTO(String id, String hackathonId, String hackathonNome, String teamId, String teamNome,
            String mentoreId, String mentoreNome, String descrizione, LocalDateTime dataSegnalazione) {
        this.id = id;
        this.hackathonId = hackathonId;
        this.hackathonNome = hackathonNome;
        this.teamId = teamId;
        this.teamNome = teamNome;
        this.mentoreId = mentoreId;
        this.mentoreNome = mentoreNome;
        this.descrizione = descrizione;
        this.dataSegnalazione = dataSegnalazione;
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

    public String getMentoreId() {
        return mentoreId;
    }

    public String getMentoreNome() {
        return mentoreNome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public LocalDateTime getDataSegnalazione() {
        return dataSegnalazione;
    }
}
