package Core.POJO_Entities;

import Core.Enums.StatoHackathon;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Hackathon {
    private String id;
    private String nome;
    private String regolamento;
    private LocalDateTime inizioIscrizioni;
    private LocalDateTime scadenzaIscrizioni;
    private LocalDateTime dataInizio;
    private LocalDateTime dataFine;
    private String luogo;
    private double premioInDenaro;
    private int dimensioneMaxTeam;
    private StatoHackathon stato;
    private LocalDateTime dataCreazione;

    // Riferimenti diretti a User
    private User organizzatore;
    private User giudice;

    // Liste
    private List<User> mentori;
    private List<Team> teamIscritti;

    // COSTRUTTORE SOLO A SCOPO DI TEST
    public Hackathon(String nome, String regolamento, LocalDateTime inizioIscrizioni,
            LocalDateTime scadenzaIscrizioni, LocalDateTime dataInizio,
            LocalDateTime dataFine, String luogo, double premioInDenaro,
            int dimensioneMaxTeam, User organizzatore, User giudice, List<User> mentori,
            StatoHackathon stato) {

        // Genera un ID Randomico
        this.id = UUID.randomUUID().toString();
        this.nome = nome;
        this.regolamento = regolamento;
        this.inizioIscrizioni = inizioIscrizioni;
        this.scadenzaIscrizioni = scadenzaIscrizioni;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.luogo = luogo;
        this.premioInDenaro = premioInDenaro;
        this.dimensioneMaxTeam = dimensioneMaxTeam;
        this.organizzatore = organizzatore;
        this.giudice = giudice;
        this.mentori = mentori;
        this.stato = stato;

        // Default
        this.dataCreazione = LocalDateTime.now();
        this.teamIscritti = new ArrayList<>();
    }

    // Getters e Setters essenziali per il Service
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

    public StatoHackathon getStato() {
        return stato;
    }

    public LocalDateTime getDataInizio() {
        return dataInizio;
    }

    public LocalDateTime getDataFine() {
        return dataFine;
    }
}
