package Core.POJO_Entities;

import Core.Enums.Ruolo;
import java.util.UUID;

public class User {
    private String id;
    private String nome;
    private String cognome;
    private String email;
    private Ruolo ruolo;

    public User(String nome, String cognome, String email, Ruolo ruolo) {
        // Genera un ID Randomico
        this.id = UUID.randomUUID().toString();
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.ruolo = ruolo;
    }

    // COSTRUTTORE SOLO A SCOPO DI TEST
    public User(String id, String nome, String cognome, String email, Ruolo ruolo) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("ID non può essere nullo o vuoto per questo costruttore");
        }
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.ruolo = ruolo;
    }

    // Getters
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
