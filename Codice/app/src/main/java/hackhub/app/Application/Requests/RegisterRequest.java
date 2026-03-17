package hackhub.app.Application.Requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RegisterRequest {

  @JsonProperty("nome")
  private String nome;

  @JsonProperty("cognome")
  private String cognome;

  @JsonProperty("email")
  private String email;

  @JsonProperty("password")
  private String password;

  public RegisterRequest() {}

  public RegisterRequest(
    String nome,
    String cognome,
    String email,
    String password
  ) {
    this.nome = nome;
    this.cognome = cognome;
    this.email = email;
    this.password = password;
  }

  public String getNome() {
    return nome;
  }

  public String getCognome() {
    return cognome;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }
}
