package hackhub.app.Core.Builders;

import java.time.LocalDateTime;
import java.util.List;
import hackhub.app.Core.Enums.StatoHackathon;
import hackhub.app.Core.POJO_Entities.User;
import hackhub.app.Core.POJO_Entities.Hackathon;

/**
 * Implementazione del pattern Builder per Hackathon.
 * Fornisce un'API fluida e type-safe per la creazione di oggetti Hackathon.
 */
public class HackathonBuilder {
    private String nome;
    private String regolamento;
    private LocalDateTime inizioIscrizioni;
    private LocalDateTime scadenzaIscrizioni;
    private LocalDateTime dataInizio;
    private LocalDateTime dataFine;
    private String luogo;
    private String logoUrl;
    private double premioInDenaro;
    private User organizzatore;
    private User giudice;
    private List<User> mentori;
    private StatoHackathon stato;

    public HackathonBuilder() {}

    public HackathonBuilder setNome(String nome) {
        this.nome = nome;
        return this;
    }

    public HackathonBuilder setRegolamento(String regolamento) {
        this.regolamento = regolamento;
        return this;
    }

    public HackathonBuilder setPeriodoIscrizione(LocalDateTime inizio, LocalDateTime scadenza) {
        this.inizioIscrizioni = inizio;
        this.scadenzaIscrizioni = scadenza;
        return this;
    }

    public HackathonBuilder setDurata(LocalDateTime inizio, LocalDateTime fine) {
        this.dataInizio = inizio;
        this.dataFine = fine;
        return this;
    }

    public HackathonBuilder setLuogo(String luogo) {
        this.luogo = luogo;
        return this;
    }

    public HackathonBuilder setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
        return this;
    }

    public HackathonBuilder setPremioInDenaro(double premioInDenaro) {
        this.premioInDenaro = premioInDenaro;
        return this;
    }

    public HackathonBuilder setOrganizzatore(User organizzatore) {
        this.organizzatore = organizzatore;
        return this;
    }

    public HackathonBuilder setGiudice(User giudice) {
        this.giudice = giudice;
        return this;
    }

    public HackathonBuilder setMentori(List<User> mentori) {
        this.mentori = mentori;
        return this;
    }

    public HackathonBuilder setStato(StatoHackathon stato) {
        this.stato = stato;
        return this;
    }

    /**
     * Costruisce l'oggetto Hackathon con tutti i parametri impostati.
     * 
     * @return Un'istanza di Hackathon completamente configurata.
     */
    public Hackathon build() {
        Hackathon hackathon = new Hackathon();
        hackathon.setNome(this.nome);
        hackathon.setRegolamento(this.regolamento);
        hackathon.setPeriodoIscrizione(this.inizioIscrizioni, this.scadenzaIscrizioni);
        hackathon.setDurata(this.dataInizio, this.dataFine);
        hackathon.setLuogo(this.luogo);
        hackathon.setLogoUrl(this.logoUrl);
        hackathon.setPremioInDenaro(this.premioInDenaro);
        hackathon.setOrganizzatore(this.organizzatore);
        hackathon.setGiudice(this.giudice);
        hackathon.setMentori(this.mentori);
        hackathon.setStato(this.stato);
        return hackathon;
    }
}
