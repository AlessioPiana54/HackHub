package Core.POJO_Entities;

import java.time.LocalDateTime;
import java.util.UUID;

public class Segnalazione {
    private String id;
    private Partecipazione partecipazione;
    private User mentore;
    private String descrizione;
    private LocalDateTime dataSegnalazione;

    public Segnalazione(Partecipazione partecipazione, User mentore, String descrizione) {
        this.id = UUID.randomUUID().toString();
        this.partecipazione = partecipazione;
        this.mentore = mentore;
        this.descrizione = descrizione;
        this.dataSegnalazione = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public Partecipazione getPartecipazione() {
        return partecipazione;
    }

    public User getMentore() {
        return mentore;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public LocalDateTime getDataSegnalazione() {
        return dataSegnalazione;
    }

    public Team getTeam() {
        return partecipazione.getTeam();
    }
}
