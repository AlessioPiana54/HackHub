package hackhub.app.Application.DTOs;

/**
 * Rappresentazione della valutazione di una sottomissione esposta dalle API.
 */
public class ValutazioneDTO {
    private String id;
    private String sottomissioneId;
    private String teamId;
    private String teamNome;
    private String giudiceId;
    private String giudiceNome;
    private double voto;
    private String giudizio;

    public ValutazioneDTO(
        String id,
        String sottomissioneId,
        String teamId,
        String teamNome,
        String giudiceId,
        String giudiceNome,
        double voto,
        String giudizio
    ) {
        this.id = id;
        this.sottomissioneId = sottomissioneId;
        this.teamId = teamId;
        this.teamNome = teamNome;
        this.giudiceId = giudiceId;
        this.giudiceNome = giudiceNome;
        this.voto = voto;
        this.giudizio = giudizio;
    }

    public String getId() { return id; }
    public String getSottomissioneId() { return sottomissioneId; }
    public String getTeamId() { return teamId; }
    public String getTeamNome() { return teamNome; }
    public String getGiudiceId() { return giudiceId; }
    public String getGiudiceNome() { return giudiceNome; }
    public double getVoto() { return voto; }
    public String getGiudizio() { return giudizio; }
}
