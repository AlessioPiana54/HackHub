package hackhub.app.Core.POJO_Entities;

import hackhub.app.Core.Enums.Ruolo;
import jakarta.persistence.*;

/**
 * Rappresenta un utente registrato nel sistema.
 * <p>
 * Un utente può avere diversi ruoli (es. Organizzatore, Giudice, Mentore,
 * Partecipante).
 * </p>
 */
@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private String nome;
  private String cognome;
  private String email;
  private String password;

  @Enumerated(EnumType.STRING)
  private Ruolo ruolo;

  public User() {}

  public User(
    String nome,
    String cognome,
    String email,
    String password,
    Ruolo ruolo
  ) {
    this.nome = nome;
    this.cognome = cognome;
    this.email = email;
    this.password = password;
    this.ruolo = ruolo;
  }

  public String getId() {
    return id;
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

  public Ruolo getRuolo() {
    return ruolo;
  }

  public void setRuolo(Ruolo ruolo) {
    this.ruolo = ruolo;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public void setCognome(String cognome) {
    this.cognome = cognome;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setId(String id) {
    this.id = id;
  }
}
