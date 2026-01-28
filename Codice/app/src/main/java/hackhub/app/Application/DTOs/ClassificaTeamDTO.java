package hackhub.app.Application.DTOs;

public class ClassificaTeamDTO {
    private String teamId;
    private String nomeTeam;
    private Double punteggio;

    public ClassificaTeamDTO(String teamId, String nomeTeam, Double punteggio) {
        this.teamId = teamId;
        this.nomeTeam = nomeTeam;
        this.punteggio = punteggio;
    }

    public String getTeamId() {
        return teamId;
    }

    public String getNomeTeam() {
        return nomeTeam;
    }

    public Double getPunteggio() {
        return punteggio;
    }
}
