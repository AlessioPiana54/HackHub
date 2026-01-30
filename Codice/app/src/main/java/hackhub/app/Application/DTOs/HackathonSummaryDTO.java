package hackhub.app.Application.DTOs;

import java.time.LocalDateTime;
import hackhub.app.Core.Enums.StatoHackathon;

public class HackathonSummaryDTO {
    private String nome;
    private String regolamento;
    private LocalDateTime dataInizio;
    private LocalDateTime dataFine;
    private String luogo;
    private double premioInDenaro;
    private StatoHackathon stato;
    private String organizzatoreNome;

    public HackathonSummaryDTO(String nome, String regolamento, LocalDateTime dataInizio, LocalDateTime dataFine,
            String luogo, double premioInDenaro, StatoHackathon stato, String organizzatoreNome) {
        this.nome = nome;
        this.regolamento = regolamento;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.luogo = luogo;
        this.premioInDenaro = premioInDenaro;
        this.stato = stato;
        this.organizzatoreNome = organizzatoreNome;
    }

    // Getters
    public String getNome() {
        return nome;
    }

    public String getRegolamento() {
        return regolamento;
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

    public StatoHackathon getStato() {
        return stato;
    }

    public String getOrganizzatoreNome() {
        return organizzatoreNome;
    }
}
