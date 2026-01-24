package hackhub.app.Core.POJO_Entities;

import jakarta.persistence.*;
import hackhub.app.Core.Enums.Ruolo;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String nome;
    private String cognome;
    private String email;

    @Enumerated(EnumType.STRING)
    private Ruolo ruolo;

    public User() {
    }

    public User(String nome, String cognome, String email, Ruolo ruolo) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.ruolo = ruolo;
    }

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

    public void setRuolo(Ruolo ruolo) {
        this.ruolo = ruolo;
    }
}
