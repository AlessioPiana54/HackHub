package hackhub.app.Application.Services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import hackhub.app.Application.IUnitOfWork.IUnitOfWork;
import hackhub.app.Application.Requests.InviaSottomissioneRequest;
import hackhub.app.Application.Requests.CreaValutazioneRequest;
import hackhub.app.Core.Enums.StatoHackathon;
import hackhub.app.Core.POJO_Entities.Hackathon;
import hackhub.app.Core.POJO_Entities.Partecipazione;
import hackhub.app.Core.POJO_Entities.Sottomissione;
import hackhub.app.Core.POJO_Entities.User;
import hackhub.app.Core.POJO_Entities.Valutazione;

@Service
@Transactional
public class SottomissioneService {
    private final IUnitOfWork unitOfWork;

    @Autowired
    public SottomissioneService(IUnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    public Sottomissione inviaSottomissione(InviaSottomissioneRequest request) {
        User utente = unitOfWork.userRepository().findById(request.getIdUtente())
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        Partecipazione partecipazioneAttiva = unitOfWork.partecipazioneRepository()
                .findByTeamIdAndHackathonId(request.getIdTeam(), request.getIdHackathon())
                .orElseThrow(() -> new IllegalArgumentException("Il team non partecipa a questo hackathon."));

        Hackathon hackathon = partecipazioneAttiva.getHackathon();
        if (hackathon.getStato() != StatoHackathon.IN_CORSO) {
            throw new IllegalStateException("L'Hackathon non è in corso, impossibile inviare sottomissioni.");
        }

        boolean isMembro = partecipazioneAttiva.getTeam().getMembri().stream()
                .anyMatch(u -> u.getId().equals(utente.getId()));
        boolean isLeader = partecipazioneAttiva.getTeam().getLeaderSquadra().getId().equals(utente.getId());

        if (!isMembro && !isLeader) {
            throw new SecurityException("L'utente non fa parte del team.");
        }

        Sottomissione sottomissione = new Sottomissione(partecipazioneAttiva, utente, request.getLinkProgetto(),
                request.getDescrizione());
        unitOfWork.sottomissioneRepository().save(sottomissione);
        return sottomissione;
    }

    public Valutazione valutaSottomissione(CreaValutazioneRequest request) {
        Sottomissione sottomissione = unitOfWork.sottomissioneRepository().findById(request.getIdSottomissione())
                .orElseThrow(() -> new IllegalArgumentException("Sottomissione non trovata"));

        Hackathon hackathon = sottomissione.getPartecipazione().getHackathon();

        if (hackathon.getStato() != StatoHackathon.IN_VALUTAZIONE) {
            throw new IllegalStateException("L'Hackathon non è in fase di valutazione.");
        }

        User giudice = unitOfWork.userRepository().findById(request.getIdGiudice())
                .orElseThrow(() -> new IllegalArgumentException("Giudice non trovato"));

        if (!hackathon.getGiudice().getId().equals(giudice.getId())) {
            throw new SecurityException("Solo il giudice dell'hackathon può valutare.");
        }

        if (unitOfWork.valutazioneRepository().existsBySottomissioneId(sottomissione.getId())) {
            throw new IllegalArgumentException("Questa sottomissione è già stata valutata.");
        }

        Valutazione valutazione = new Valutazione(giudice, request.getGiudizio(), request.getVoto());
        valutazione.setSottomissione(sottomissione);

        unitOfWork.valutazioneRepository().save(valutazione);
        return valutazione;
    }
}
