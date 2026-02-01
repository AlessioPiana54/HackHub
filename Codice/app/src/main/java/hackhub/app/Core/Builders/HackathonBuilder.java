package hackhub.app.Core.Builders;

import java.time.LocalDateTime;
import java.util.List;

import hackhub.app.Core.Enums.StatoHackathon;
import hackhub.app.Core.POJO_Entities.Hackathon;
import hackhub.app.Core.POJO_Entities.User;

/**
 * Implementazione concreta del pattern Builder per creare istanze di Hackathon.
 */
public class HackathonBuilder implements IHackathonBuilder {
    private String nome;
    private String regolamento;
    private LocalDateTime inizioIscrizioni;
    private LocalDateTime scadenzaIscrizioni;
    private LocalDateTime dataInizio;
    private LocalDateTime dataFine;
    private String luogo;
    private double premioInDenaro;
    private User organizzatore;
    private User giudice;
    private List<User> mentori;
    private StatoHackathon stato;

    @Override
    public IHackathonBuilder setNome(String nome) {
        this.nome = nome;
        return this;
    }

    @Override
    public IHackathonBuilder setRegolamento(String regolamento) {
        this.regolamento = regolamento;
        return this;
    }

    @Override
    public IHackathonBuilder setPeriodoIscrizione(LocalDateTime inizio, LocalDateTime scadenza) {
        this.inizioIscrizioni = inizio;
        this.scadenzaIscrizioni = scadenza;
        return this;
    }

    @Override
    public IHackathonBuilder setDurata(LocalDateTime inizio, LocalDateTime fine) {
        this.dataInizio = inizio;
        this.dataFine = fine;
        return this;
    }

    @Override
    public IHackathonBuilder setLuogo(String luogo) {
        this.luogo = luogo;
        return this;
    }

    @Override
    public IHackathonBuilder setPremioInDenaro(double premioInDenaro) {
        this.premioInDenaro = premioInDenaro;
        return this;
    }

    @Override
    public IHackathonBuilder setOrganizzatore(User organizzatore) {
        this.organizzatore = organizzatore;
        return this;
    }

    @Override
    public IHackathonBuilder setGiudice(User giudice) {
        this.giudice = giudice;
        return this;
    }

    @Override
    public IHackathonBuilder setMentori(List<User> mentori) {
        this.mentori = mentori;
        return this;
    }

    @Override
    public IHackathonBuilder setStato(StatoHackathon stato) {
        this.stato = stato;
        return this;
    }

    /**
     * Costruisce l'oggetto Hackathon utilizzando i parametri configurati.
     *
     * @return una nuova istanza di Hackathon
     */
    @Override
    public Hackathon build() {
        return new Hackathon(
                nome,
                regolamento,
                inizioIscrizioni,
                scadenzaIscrizioni,
                dataInizio,
                dataFine,
                luogo,
                premioInDenaro,
                organizzatore,
                giudice,
                mentori,
                stato);
    }
}
