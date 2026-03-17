package hackhub.app.Application.DTOs;

import hackhub.app.Core.Enums.Ruolo;

/**
 * DTO per i dati pubblici dell'utente
 */
public class UserDTO {
    private String id;
    private String nome;
    private String cognome;
    private String email;
    private Ruolo ruolo;
    
    public UserDTO() {}

    public UserDTO(String id, String nome, String cognome, String email, Ruolo ruolo) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
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

    public Ruolo getRuolo() {
        return ruolo;
    }
}
