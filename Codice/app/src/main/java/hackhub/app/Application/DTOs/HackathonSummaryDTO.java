package hackhub.app.Application.DTOs;

import hackhub.app.Core.Enums.StatoHackathon;
import java.time.LocalDateTime;

/**
 * DTO sintetico per la visualizzazione nella lista degli Hackathon.
 */
public class HackathonSummaryDTO {

  private String id;
  private String nome;
  private String regolamento;
  private String descrizione;
  private String logoUrl;
  private LocalDateTime inizioIscrizioni;
  private LocalDateTime scadenzaIscrizioni;
  private LocalDateTime dataInizio;
  private LocalDateTime dataFine;
  private String luogo;
  private double premioInDenaro;
  private StatoHackathon stato;
  private String organizzatoreNome;

  public HackathonSummaryDTO(
    String id,
    String nome,
    String regolamento,
    String descrizione,
    String logoUrl,
    LocalDateTime inizioIscrizioni,
    LocalDateTime scadenzaIscrizioni,
    LocalDateTime dataInizio,
    LocalDateTime dataFine,
    String luogo,
    double premioInDenaro,
    StatoHackathon stato,
    String organizzatoreNome
  ) {
    this.id = id;
    this.nome = nome;
    this.regolamento = regolamento;
    this.descrizione = descrizione;
    this.logoUrl = logoUrl;
    this.inizioIscrizioni = inizioIscrizioni;
    this.scadenzaIscrizioni = scadenzaIscrizioni;
    this.dataInizio = dataInizio;
    this.dataFine = dataFine;
    this.luogo = luogo;
    this.premioInDenaro = premioInDenaro;
    this.stato = stato;
    this.organizzatoreNome = organizzatoreNome;
  }

  // Getters
  public String getId() {
    return id;
  }

  public String getNome() {
    return nome;
  }

  public String getRegolamento() {
    return regolamento;
  }

  public String getDescrizione() {
    return descrizione;
  }

  public String getLogoUrl() {
    return logoUrl;
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

  public StatoHackathon getStato() {
    return stato;
  }

  public String getOrganizzatoreNome() {
    return organizzatoreNome;
  }
}
