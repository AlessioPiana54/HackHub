package hackhub.app.Application.Scheduler;

import hackhub.app.Application.IRepositories.IHackathonRepository;
import hackhub.app.Core.Enums.StatoHackathon;
import hackhub.app.Core.POJO_Entities.Hackathon;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class HackathonScheduler {

    private final IHackathonRepository hackathonRepository;

    public HackathonScheduler(IHackathonRepository hackathonRepository) {
        this.hackathonRepository = hackathonRepository;
    }

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

    private boolean processHackathonState(Hackathon hackathon, LocalDateTime now) {
        return switch (hackathon.getStato()) {
            case IN_ATTESA -> manageInAttesa(hackathon, now);
            case IN_ISCRIZIONE -> manageInIscrizione(hackathon, now);
            case IN_CORSO -> manageInCorso(hackathon, now);
            default -> false;
        };
    }

    private boolean manageInAttesa(Hackathon hackathon, LocalDateTime now) {
        if (now.isAfter(hackathon.getInizioIscrizioni()) && now.isBefore(hackathon.getScadenzaIscrizioni())) {
            hackathon.setStato(StatoHackathon.IN_ISCRIZIONE);
            return true;
        } else if (now.isAfter(hackathon.getDataInizio()) && now.isBefore(hackathon.getDataFine())) {
            hackathon.setStato(StatoHackathon.IN_CORSO);
            return true;
        }
        return false;
    }

    private boolean manageInIscrizione(Hackathon hackathon, LocalDateTime now) {
        if (now.isAfter(hackathon.getScadenzaIscrizioni()) && now.isBefore(hackathon.getDataInizio())) {
            hackathon.setStato(StatoHackathon.IN_ATTESA);
            return true;
        } else if (now.isAfter(hackathon.getDataInizio()) && now.isBefore(hackathon.getDataFine())) {
            hackathon.setStato(StatoHackathon.IN_CORSO);
            return true;
        }
        return false;
    }

    private boolean manageInCorso(Hackathon hackathon, LocalDateTime now) {
        if (now.isAfter(hackathon.getDataFine())) {
            hackathon.setStato(StatoHackathon.IN_VALUTAZIONE);
            return true;
        }
        return false;
    }
}
