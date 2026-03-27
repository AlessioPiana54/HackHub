package hackhub.app.Application.DTOs;

import java.time.LocalDateTime;

/**
 * DTO per i dati di una sottomissione.
 */
public class SottomissioneDTO {
    private String id;
    private String hackathonId;
    private String teamId;
    private String teamNome;
    private String mittenteId;
    private String mittenteNome;
    private String linkProgetto;
    private String descrizione;
    private LocalDateTime dataSottomissione;

    public SottomissioneDTO(
        String id,
        String hackathonId,
        String teamId,
        String teamNome,
        String mittenteId,
        String mittenteNome,
        String linkProgetto,
        String descrizione,
        LocalDateTime dataSottomissione
    ) {
        this.id = id;
        this.hackathonId = hackathonId;
        this.teamId = teamId;
        this.teamNome = teamNome;
        this.mittenteId = mittenteId;
        this.mittenteNome = mittenteNome;
        this.linkProgetto = linkProgetto;
        this.descrizione = descrizione;
        this.dataSottomissione = dataSottomissione;
    }

    public String getId() { return id; }
    public String getHackathonId() { return hackathonId; }
    public String getTeamId() { return teamId; }
    public String getTeamNome() { return teamNome; }
    public String getMittenteId() { return mittenteId; }
    public String getMittenteNome() { return mittenteNome; }
    public String getLinkProgetto() { return linkProgetto; }
    public String getDescrizione() { return descrizione; }
    public LocalDateTime getDataSottomissione() { return dataSottomissione; }
}
