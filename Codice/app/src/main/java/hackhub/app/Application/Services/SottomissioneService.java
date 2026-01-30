package hackhub.app.Application.Services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import hackhub.app.Application.IUnitOfWork.IUnitOfWork;
import hackhub.app.Application.Requests.InviaSottomissioneRequest;
import hackhub.app.Application.Requests.ModificaSottomissioneRequest;
import hackhub.app.Application.Strategies.LinkStrategyContext;
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
    private final LinkStrategyContext linkStrategyContext;

    @Autowired
    public SottomissioneService(IUnitOfWork unitOfWork,
            LinkStrategyContext linkStrategyContext) {
        this.unitOfWork = unitOfWork;
        this.linkStrategyContext = linkStrategyContext;
    }

    public Sottomissione inviaSottomissione(InviaSottomissioneRequest request, String utenteId) {
        if (!linkStrategyContext.validate(request.getLinkProgetto(), List.of("GitHub"))) {
            throw new IllegalArgumentException("Il link del progetto deve essere un link GitHub valido.");
        }
        User utente = unitOfWork.userRepository().findById(utenteId)
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

        if (unitOfWork.sottomissioneRepository().existsByPartecipazioneId(partecipazioneAttiva.getId())) {
            throw new IllegalStateException("Il team ha già inviato una sottomissione per questo hackathon.");
        }

        Sottomissione sottomissione = new Sottomissione(partecipazioneAttiva, utente, request.getLinkProgetto(),
                request.getDescrizione());
        unitOfWork.sottomissioneRepository().save(sottomissione);
        return sottomissione;
    }

    public Valutazione valutaSottomissione(CreaValutazioneRequest request, String giudiceId) {
        Sottomissione sottomissione = unitOfWork.sottomissioneRepository().findById(request.getIdSottomissione())
                .orElseThrow(() -> new IllegalArgumentException("Sottomissione non trovata"));

        Hackathon hackathon = sottomissione.getPartecipazione().getHackathon();

        if (hackathon.getStato() != StatoHackathon.IN_VALUTAZIONE) {
            throw new IllegalStateException("L'Hackathon non è in fase di valutazione.");
        }

        User giudice = unitOfWork.userRepository().findById(giudiceId)
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

    public Sottomissione modificaSottomissione(ModificaSottomissioneRequest request, String utenteId) {
        Sottomissione sottomissione = unitOfWork.sottomissioneRepository().findById(request.getIdSottomissione())
                .orElseThrow(() -> new IllegalArgumentException("Sottomissione non trovata"));

        if (!linkStrategyContext.validate(request.getLinkProgetto(), List.of("GitHub"))) {
            throw new IllegalArgumentException("Il link del progetto deve essere un link GitHub valido.");
        }

        Hackathon hackathon = sottomissione.getPartecipazione().getHackathon();
        if (hackathon.getStato() != StatoHackathon.IN_CORSO) {
            throw new IllegalStateException("L'Hackathon non è in corso, impossibile modificare la sottomissione.");
        }

        User utente = unitOfWork.userRepository().findById(utenteId)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        boolean isMembro = sottomissione.getPartecipazione().getTeam().getMembri().stream()
                .anyMatch(u -> u.getId().equals(utente.getId()));
        boolean isLeader = sottomissione.getPartecipazione().getTeam().getLeaderSquadra().getId()
                .equals(utente.getId());

        if (!isMembro && !isLeader) {
            throw new SecurityException("L'utente non fa parte del team.");
        }

        sottomissione.setLinkProgetto(request.getLinkProgetto());
        sottomissione.setDescrizione(request.getDescrizione());

        unitOfWork.sottomissioneRepository().save(sottomissione);
        return sottomissione;
    }
}
