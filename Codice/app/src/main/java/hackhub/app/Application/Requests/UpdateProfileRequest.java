package hackhub.app.Application.Requests;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request per l'aggiornamento del profilo utente
 */
public class UpdateProfileRequest {

  @JsonProperty("nome")
  private String nome;

  @JsonProperty("cognome")
  private String cognome;

  public UpdateProfileRequest() {}

  public UpdateProfileRequest(String nome, String cognome) {
    this.nome = nome;
    this.cognome = cognome;
  }

  public String getNome() {
    return nome;
  }

  public String getCognome() {
    return cognome;
  }
}
