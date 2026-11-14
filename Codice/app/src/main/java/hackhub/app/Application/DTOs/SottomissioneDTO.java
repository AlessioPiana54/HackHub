package hackhub.app.Application.DTOs;

import java.time.LocalDateTime;

/**
 * Rappresentazione di una sottomissione esposta dalle API.
 * I campi {@code voto} e {@code giudizio} sono valorizzati solo dopo la
 * valutazione del giudice.
 */
public class SottomissioneDTO {
    private String id;
    private String hackathonId;
    private String hackathonNome;
    private String teamId;
    private String teamNome;
    private String mittenteId;
    private String mittenteNome;
    private String linkProgetto;
    private String descrizione;
    private LocalDateTime dataSottomissione;
    private Double voto;
    private String giudizio;

    public SottomissioneDTO(
        String id,
        String hackathonId,
        String hackathonNome,
        String teamId,
        String teamNome,
        String mittenteId,
        String mittenteNome,
        String linkProgetto,
        String descrizione,
        LocalDateTime dataSottomissione,
        Double voto,
        String giudizio
    ) {
        this.id = id;
        this.hackathonId = hackathonId;
        this.hackathonNome = hackathonNome;
        this.teamId = teamId;
        this.teamNome = teamNome;
        this.mittenteId = mittenteId;
        this.mittenteNome = mittenteNome;
        this.linkProgetto = linkProgetto;
        this.descrizione = descrizione;
        this.dataSottomissione = dataSottomissione;
        this.voto = voto;
        this.giudizio = giudizio;
    }

    public String getId() { return id; }
    public String getHackathonId() { return hackathonId; }
    public String getHackathonNome() { return hackathonNome; }
    public String getTeamId() { return teamId; }
    public String getTeamNome() { return teamNome; }
    public String getMittenteId() { return mittenteId; }
    public String getMittenteNome() { return mittenteNome; }
    public String getLinkProgetto() { return linkProgetto; }
    public String getDescrizione() { return descrizione; }
    public LocalDateTime getDataSottomissione() { return dataSottomissione; }
    public Double getVoto() { return voto; }
    public String getGiudizio() { return giudizio; }
}
