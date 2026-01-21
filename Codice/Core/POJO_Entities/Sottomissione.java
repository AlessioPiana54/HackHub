package Core.POJO_Entities;

import java.time.LocalDateTime;
import java.util.UUID;

public class Sottomissione {
    private String id;
    private Hackathon hackathon;
    private Team team;
    private User mittente; // Membro o Leader
    private String linkProgetto; // URL repository o altro
    private String descrizione;
    private LocalDateTime dataSottomissione;
    private Valutazione valutazione;

    // COSTRUTTORE SOLO A SCOPO DI TEST
    public Sottomissione(Hackathon hackathon, Team team, User mittente, String linkProgetto, String descrizione) {
        this.id = UUID.randomUUID().toString();
        this.hackathon = hackathon;
        this.team = team;
        this.mittente = mittente;
        this.linkProgetto = linkProgetto;
        this.descrizione = descrizione;

        // Defaults
        this.dataSottomissione = LocalDateTime.now();
    }

    // Getters
    public String getId() {
        return id;
    }

    public Hackathon getHackathon() {
        return hackathon;
    }

    public Team getTeam() {
        return team;
    }

    public User getMittente() {
        return mittente;
    }

    public String getLinkProgetto() {
        return linkProgetto;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public LocalDateTime getDataSottomissione() {
        return dataSottomissione;
    }

    public Valutazione getValutazione() {
        return valutazione;
    }

    public void setValutazione(Valutazione valutazione) {
        this.valutazione = valutazione;
    }
}
