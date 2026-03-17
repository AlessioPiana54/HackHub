package hackhub.app.Application.DTOs;

import java.time.LocalDateTime;

/**
 * DTO per i dati dell'invito
 */
public class InvitoDTO {

  private String id;
  private String mittenteId;
  private String mittenteNome;
  private String destinatarioId;
  private String destinatarioNome;
  private String teamId;
  private String teamNome;
  private LocalDateTime dataInvio;

  public InvitoDTO(
    String id,
    String mittenteId,
    String mittenteNome,
    String destinatarioId,
    String destinatarioNome,
    String teamId,
    String teamNome,
    LocalDateTime dataInvio
  ) {
    this.id = id;
    this.mittenteId = mittenteId;
    this.mittenteNome = mittenteNome;
    this.destinatarioId = destinatarioId;
    this.destinatarioNome = destinatarioNome;
    this.teamId = teamId;
    this.teamNome = teamNome;
    this.dataInvio = dataInvio;
  }

  public String getId() {
    return id;
  }

  public String getMittenteId() {
    return mittenteId;
  }

  public String getMittenteNome() {
    return mittenteNome;
  }

  public String getDestinatarioId() {
    return destinatarioId;
  }

  public String getDestinatarioNome() {
    return destinatarioNome;
  }

  public String getTeamId() {
    return teamId;
  }

  public String getTeamNome() {
    return teamNome;
  }

  public LocalDateTime getDataInvio() {
    return dataInvio;
  }
}
