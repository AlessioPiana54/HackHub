package hackhub.app.Application.Scheduler;

import hackhub.app.Application.IRepositories.IHackathonRepository;
import hackhub.app.Core.Enums.StatoHackathon;
import hackhub.app.Core.POJO_Entities.Hackathon;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Componente scheduler per la gestione automatica degli stati degli Hackathon.
 * Esegue un task periodico per aggiornare lo stato di ogni Hackathon in base
 * alla data e ora corrente.
 */
@Component
public class HackathonScheduler {

    private final IHackathonRepository hackathonRepository;

    public HackathonScheduler(IHackathonRepository hackathonRepository) {
        this.hackathonRepository = hackathonRepository;
    }

    /**
     * Metodo pianificato che viene eseguito ogni minuto.
     * Recupera gli hackathon attivi (escludendo quelli conclusi, in valutazione o
     * in premiazione)
     * e aggiorna il loro stato se necessario.
     */
    @Scheduled(cron = "0 * * * * *") // Runs every minute
    @Transactional
    public void updateHackathonStates() {
        List<StatoHackathon> statiEsclusi = List.of(
                StatoHackathon.CONCLUSO,
                StatoHackathon.IN_VALUTAZIONE,
                StatoHackathon.IN_PREMIAZIONE);
        List<Hackathon> hackathons = hackathonRepository.findByStatoNotIn(statiEsclusi);
        LocalDateTime now = LocalDateTime.now();

        for (Hackathon hackathon : hackathons) {
            boolean changed = processHackathonState(hackathon, now);
            if (changed) {
                hackathonRepository.save(hackathon);
            }
        }
    }

    /**
     * Elabora lo stato di un singolo Hackathon in base al suo stato attuale e
     * all'ora corrente.
     *
     * @param hackathon l'Hackathon da processare
     * @param now       la data e ora corrente
     * @return true se lo stato è cambiato, false altrimenti
     */
    private boolean processHackathonState(Hackathon hackathon, LocalDateTime now) {
        return switch (hackathon.getStato()) {
            case IN_ATTESA -> manageInAttesa(hackathon, now);
            case IN_ISCRIZIONE -> manageInIscrizione(hackathon, now);
            case IN_CORSO -> manageInCorso(hackathon, now);
            default -> false;
        };
    }

    /**
     * Gestisce le transizioni di stato per un Hackathon nello stato IN_ATTESA.
     * Transizioni possibili:
     * - IN_ISCRIZIONE: se l'ora attuale è compresa tra inizio e scadenza
     * iscrizioni.
     * - IN_CORSO: se l'ora attuale è compresa tra inizio e fine hackathon.
     *
     * @param hackathon l'Hackathon da controllare
     * @param now       la data e ora corrente
     * @return true se lo stato cambia
     */
    private boolean manageInAttesa(Hackathon hackathon, LocalDateTime now) {
        // Da "In Attesa" a "In Iscrizione"
        if (now.isAfter(hackathon.getInizioIscrizioni()) && now.isBefore(hackathon.getScadenzaIscrizioni())) {
            hackathon.setStato(StatoHackathon.IN_ISCRIZIONE);
            return true;
        }
        // Da "In Attesa" a "In Corso"
        else if (now.isAfter(hackathon.getDataInizio()) && now.isBefore(hackathon.getDataFine())) {
            hackathon.setStato(StatoHackathon.IN_CORSO);
            return true;
        }
        return false;
    }

    /**
     * Gestisce le transizioni di stato per un Hackathon nello stato IN_ISCRIZIONE.
     * Transizioni possibili:
     * - IN_ATTESA: se l'iscrizione è scaduta ma l'hackathon non è ancora iniziato.
     * - IN_CORSO: se l'ora attuale è compresa tra inizio e fine hackathon.
     *
     * @param hackathon l'Hackathon da controllare
     * @param now       la data e ora corrente
     * @return true se lo stato cambia
     */
    private boolean manageInIscrizione(Hackathon hackathon, LocalDateTime now) {
        // Da "In Iscrizione" a "In Attesa"
        if (now.isAfter(hackathon.getScadenzaIscrizioni()) && now.isBefore(hackathon.getDataInizio())) {
            hackathon.setStato(StatoHackathon.IN_ATTESA);
            return true;
        }
        // Da "In Iscrizione" a "In Corso"
        else if (now.isAfter(hackathon.getDataInizio()) && now.isBefore(hackathon.getDataFine())) {
            hackathon.setStato(StatoHackathon.IN_CORSO);
            return true;
        }
        return false;
    }

    /**
     * Gestisce le transizioni di stato per un Hackathon nello stato IN_CORSO.
     * Transizioni possibili:
     * - IN_VALUTAZIONE: se l'hackathon è terminato (data fine passata).
     *
     * @param hackathon l'Hackathon da controllare
     * @param now       la data e ora corrente
     * @return true se lo stato cambia
     */
    private boolean manageInCorso(Hackathon hackathon, LocalDateTime now) {
        // Da "In Corso" a "In Valutazione"
        if (now.isAfter(hackathon.getDataFine())) {
            hackathon.setStato(StatoHackathon.IN_VALUTAZIONE);
            return true;
        }
        return false;
    }
}
