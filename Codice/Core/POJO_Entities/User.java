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

    public boolean haRuolo(Ruolo ruoloRichiesto) {
        return this.ruolo == ruoloRichiesto;
    }

    // Getters
    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getCognome() { return cognome; }
    public String getEmail() { return email; }
    public Ruolo getRuolo() { return ruolo; }
}
