package hackhub.app.Application.Requests;

import java.time.LocalDateTime;
import java.util.List;

public class CreaHackathonRequest {
    private String nome;
    private String regolamento;
    private LocalDateTime inizioIscrizioni;
    private LocalDateTime scadenzaIscrizioni;
    private LocalDateTime dataInizio;
    private LocalDateTime dataFine;
    private String luogo;
    private double premioInDenaro;
    private int dimensioneMaxTeam;
    private String idOrganizzatore;
    private String idGiudice;
    private List<String> idMentori;

    public CreaHackathonRequest(String nome, String regolamento, LocalDateTime inizioIscrizioni,
            LocalDateTime scadenzaIscrizioni, LocalDateTime dataInizio,
            LocalDateTime dataFine, String luogo, double premioInDenaro,
            int dimensioneMaxTeam, String idOrganizzatore, String idGiudice,
            List<String> idMentori) {
        this.nome = nome;
        this.regolamento = regolamento;
        this.inizioIscrizioni = inizioIscrizioni;
        this.scadenzaIscrizioni = scadenzaIscrizioni;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.luogo = luogo;
        this.premioInDenaro = premioInDenaro;
        this.dimensioneMaxTeam = dimensioneMaxTeam;
        this.idOrganizzatore = idOrganizzatore;
        this.idGiudice = idGiudice;
        this.idMentori = idMentori;
    }

    public String getNome() {
        return nome;
    }

    public String getRegolamento() {
        return regolamento;
    }

    public LocalDateTime getInizioIscrizioni() {
        return inizioIscrizioni;
    }

    public LocalDateTime getScadenzaIscrizioni() {
        return scadenzaIscrizioni;
    }

    public LocalDateTime getDataInizio() {
        return dataInizio;
    }

    public LocalDateTime getDataFine() {
        return dataFine;
    }

    public String getLuogo() {
        return luogo;
    }

    public double getPremioInDenaro() {
        return premioInDenaro;
    }

    public int getDimensioneMaxTeam() {
        return dimensioneMaxTeam;
    }

    public String getIdOrganizzatore() {
        return idOrganizzatore;
    }

    public String getIdGiudice() {
        return idGiudice;
    }

    public List<String> getIdMentori() {
        return idMentori;
    }
}
