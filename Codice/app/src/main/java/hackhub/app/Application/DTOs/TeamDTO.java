package hackhub.app.Application.DTOs;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO per i dati del team
 */
public class TeamDTO {
    private String id;
    private String nome;
    private String leaderId;
    private String leaderNome;
    private List<String> membriIds;
    private List<String> membriNomi;
    private LocalDateTime dataCreazione;

    public TeamDTO(String id, String nome, String leaderId, String leaderNome, 
                   List<String> membriIds, List<String> membriNomi, LocalDateTime dataCreazione) {
        this.id = id;
        this.nome = nome;
        this.leaderId = leaderId;
        this.leaderNome = leaderNome;
        this.membriIds = membriIds;
        this.membriNomi = membriNomi;
        this.dataCreazione = dataCreazione;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getLeaderId() {
        return leaderId;
    }

    public String getLeaderNome() {
        return leaderNome;
    }

    public List<String> getMembriIds() {
        return membriIds;
    }

    public List<String> getMembriNomi() {
        return membriNomi;
    }

    public LocalDateTime getDataCreazione() {
        return dataCreazione;
    }
}
