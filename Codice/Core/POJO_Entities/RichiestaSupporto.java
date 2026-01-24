package Core.POJO_Entities;

import java.time.LocalDateTime;
import java.util.UUID;

public class RichiestaSupporto {
    private String id;
    private Partecipazione partecipazione;
    private User richiedente;
    private String descrizione;
    private LocalDateTime dataRichiesta;

    public RichiestaSupporto(Partecipazione partecipazione, User richiedente, String descrizione) {
        this.id = UUID.randomUUID().toString();
        this.partecipazione = partecipazione;
        this.richiedente = richiedente;
        this.descrizione = descrizione;

        this.dataRichiesta = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public Partecipazione getPartecipazione() {
        return partecipazione;
    }

    public User getRichiedente() {
        return richiedente;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public LocalDateTime getDataRichiesta() {
        return dataRichiesta;
    }
}
