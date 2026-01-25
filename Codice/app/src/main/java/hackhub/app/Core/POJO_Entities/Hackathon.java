package hackhub.app.Core.POJO_Entities;

import jakarta.persistence.*;
import java.util.List;
import java.time.LocalDateTime;
import hackhub.app.Core.Enums.StatoHackathon;

@Entity
@Table(name = "hackathons")
public class Hackathon {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String nome;
    private String regolamento;
    private LocalDateTime inizioIscrizioni;
    private LocalDateTime scadenzaIscrizioni;
    private LocalDateTime dataInizio;
    private LocalDateTime dataFine;
    private String luogo;
    private double premioInDenaro;
    private LocalDateTime dataCreazione;

    @Enumerated(EnumType.STRING)
    private StatoHackathon stato;

    // Riferimenti diretti a User
    @ManyToOne
    @JoinColumn(name = "organizzatore_id")
    private User organizzatore;

    @ManyToOne
    @JoinColumn(name = "giudice_id")
    private User giudice;

    // Liste
    @ManyToMany
    @JoinTable(name = "hackathon_mentori", joinColumns = @JoinColumn(name = "hackathon_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> mentori;

    public Hackathon() {
    }

    public Hackathon(String nome, String regolamento, LocalDateTime inizioIscrizioni,
            LocalDateTime scadenzaIscrizioni, LocalDateTime dataInizio,
            LocalDateTime dataFine, String luogo, double premioInDenaro,
            User organizzatore, User giudice, List<User> mentori,
            StatoHackathon stato) {
        this.nome = nome;
        this.regolamento = regolamento;
        this.inizioIscrizioni = inizioIscrizioni;
        this.scadenzaIscrizioni = scadenzaIscrizioni;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.luogo = luogo;
        this.premioInDenaro = premioInDenaro;
        this.organizzatore = organizzatore;
        this.giudice = giudice;
        this.mentori = mentori;
        this.stato = stato;
        this.dataCreazione = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public User getOrganizzatore() {
        return organizzatore;
    }

    public User getGiudice() {
        return giudice;
    }

    public List<User> getMentori() {
        return mentori;
    }

    public StatoHackathon getStato() {
        return stato;
    }

    public void setStato(StatoHackathon stato) {
        this.stato = stato;
    }

    public LocalDateTime getDataInizio() {
        return dataInizio;
    }

    public LocalDateTime getDataFine() {
        return dataFine;
    }

    public String getRegolamento() {
        return regolamento;
    }

    public LocalDateTime getInizioIscrizioni() {
        return inizioIscrizioni;
    }

    public LocalDateTime getScadenzaIscrizioni() {
        return scadenzaIscrizioni;
    }

    public String getLuogo() {
        return luogo;
    }

    public double getPremioInDenaro() {
        return premioInDenaro;
    }

    public LocalDateTime getDataCreazione() {
        return dataCreazione;
    }

    @Version
    private Long version;

}
