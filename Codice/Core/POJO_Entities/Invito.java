package Core.POJO_Entities;

import java.time.LocalDateTime;
import java.util.UUID;

public class Invito {
    private String id;
    private Team team;
    private User destinatario;
    private User mittente;
    private LocalDateTime dataInvito;

    public Invito(Team team, User destinatario, User mittente) {
        this.id = UUID.randomUUID().toString();
        this.team = team;
        this.destinatario = destinatario;
        this.mittente = mittente;
        this.dataInvito = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public Team getTeam() {
        return team;
    }

    public User getDestinatario() {
        return destinatario;
    }
    
    public User getMittente() {
        return mittente;
    }

    public LocalDateTime getDataInvito() {
        return dataInvito;
    }
}
