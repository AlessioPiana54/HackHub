package hackhub.app.Application.Services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import hackhub.app.Application.DTOs.ClassificaTeamDTO;
import hackhub.app.Application.IUnitOfWork.IUnitOfWork;
import hackhub.app.Application.Requests.CreaHackathonRequest;
import hackhub.app.Application.Utils.IPaymentManager;
import hackhub.app.Core.Builders.HackathonBuilder;
import hackhub.app.Core.Enums.Ruolo;
import hackhub.app.Core.Enums.StatoHackathon;
import hackhub.app.Core.POJO_Entities.Hackathon;
import hackhub.app.Core.POJO_Entities.Sottomissione;
import hackhub.app.Core.POJO_Entities.Team;
import hackhub.app.Core.POJO_Entities.User;
import hackhub.app.Core.POJO_Entities.Valutazione;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class HackathonService {

    private final IUnitOfWork unitOfWork;
    private final IPaymentManager paymentService;

    @Autowired
    public HackathonService(IUnitOfWork unitOfWork, IPaymentManager paymentService) {
        this.unitOfWork = unitOfWork;
        this.paymentService = paymentService;
    }

    public Hackathon creaHackathon(CreaHackathonRequest request, String organizzatoreId) {
        User organizzatore = unitOfWork.userRepository().findById(organizzatoreId)
                .orElseThrow(() -> new IllegalArgumentException("Organizzatore non trovato nel database."));
        User giudice = unitOfWork.userRepository().findById(request.getIdGiudice())
                .orElseThrow(() -> new IllegalArgumentException("Giudice non trovato nel database."));

        if (organizzatore.getRuolo() != Ruolo.ORGANIZZATORE) {
            throw new SecurityException("L'utente specificato come organizzatore non ha i permessi necessari.");
        }
        if (giudice.getRuolo() != Ruolo.GIUDICE) {
            throw new IllegalArgumentException("L'utente specificato come giudice non ha il ruolo di GIUDICE.");
        }

        List<User> listaMentori = unitOfWork.userRepository().findAllById(request.getIdMentori());
        if (listaMentori.size() != request.getIdMentori().size()) {
            throw new IllegalArgumentException("Uno o più mentori non trovati.");
        }

        for (User mentore : listaMentori) {
            if (mentore.getRuolo() != Ruolo.MENTORE) {
                throw new IllegalArgumentException("L'utente " + mentore.getNome() + " non ha il ruolo di MENTORE.");
            }
        }

        StatoHackathon statoIniziale;
        if (!request.getInizioIscrizioni().isAfter(LocalDateTime.now())) {
            statoIniziale = StatoHackathon.IN_ISCRIZIONE;
        } else {
            statoIniziale = StatoHackathon.IN_ATTESA;
        }

        Hackathon nuovoHackathon = new HackathonBuilder()
                .setNome(request.getNome())
                .setRegolamento(request.getRegolamento())
                .setPeriodoIscrizione(request.getInizioIscrizioni(), request.getScadenzaIscrizioni())
                .setDurata(request.getDataInizio(), request.getDataFine())
                .setLuogo(request.getLuogo())
                .setPremioInDenaro(request.getPremioInDenaro())
                .setOrganizzatore(organizzatore)
                .setGiudice(giudice)
                .setMentori(listaMentori)
                .setStato(statoIniziale)
                .build();

        unitOfWork.hackathonRepository().save(nuovoHackathon);

        return nuovoHackathon;
    }

    public void terminaFaseValutazione(String hackathonId, String giudiceId) {
        Hackathon hackathon = unitOfWork.hackathonRepository().findById(hackathonId)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon non trovato"));

        if (hackathon.getStato() != StatoHackathon.IN_VALUTAZIONE) {
            throw new IllegalStateException("L'Hackathon non è in fase di valutazione");
        }

        if (!hackathon.getGiudice().getId().equals(giudiceId)) {
            throw new SecurityException("Solo il giudice dell'Hackathon può terminare la valutazione");
        }

        List<Sottomissione> sottomissioni = unitOfWork.sottomissioneRepository()
                .findByPartecipazioneHackathonId(hackathonId);

        // Recupero tutte le valutazioni in una sola query
        List<Valutazione> valutazioni = unitOfWork.valutazioneRepository()
                .findAllBySottomissione_Partecipazione_Hackathon_Id(hackathonId);

        // Uso un Set di ID sottomissione per verificare rapidamente se una
        // sottomissione è stata valutata
        Set<String> sottomissioniValutateIds = valutazioni.stream()
                .map(v -> v.getSottomissione().getId())
                .collect(Collectors.toSet());

        for (Sottomissione s : sottomissioni) {
            if (!sottomissioniValutateIds.contains(s.getId())) {
                throw new IllegalStateException(
                        "Non tutte le sottomissioni sono state valutate. Impossibile terminare la fase di valutazione.");
            }
        }

        hackathon.setStato(StatoHackathon.IN_PREMIAZIONE);
        unitOfWork.hackathonRepository().save(hackathon);
    }

    public List<ClassificaTeamDTO> getClassifica(String hackathonId, String requesterId) {
        Hackathon hackathon = unitOfWork.hackathonRepository().findById(hackathonId)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon non trovato"));

        if (hackathon.getStato() != StatoHackathon.IN_PREMIAZIONE && hackathon.getStato() != StatoHackathon.CONCLUSO) {
            throw new IllegalStateException(
                    "La classifica è disponibile solo in fase di PREMIAZIONE o a Hackathon CONCLUSO.");
        }

        if (hackathon.getStato() == StatoHackathon.IN_PREMIAZIONE) {
            if (!hackathon.getOrganizzatore().getId().equals(requesterId)
                    && !hackathon.getGiudice().getId().equals(requesterId)) {
                throw new SecurityException(
                        "Solo l'organizzatore o il giudice dell'Hackathon possono visualizzare la classifica durante la fase di premiazione.");
            }
        }

        List<Sottomissione> sottomissioni = unitOfWork.sottomissioneRepository()
                .findByPartecipazioneHackathonId(hackathonId);

        // Recupero tutte le valutazioni in una sola query
        List<Valutazione> valutazioni = unitOfWork.valutazioneRepository()
                .findAllBySottomissione_Partecipazione_Hackathon_Id(hackathonId);

        // Creo una mappa per accesso O(1) usando l'ID della sottomissione come chiave
        Map<String, Valutazione> mappaValutazioni = valutazioni.stream()
                .collect(Collectors.toMap(v -> v.getSottomissione().getId(), v -> v));

        List<ClassificaTeamDTO> classifica = new ArrayList<>();

        for (Sottomissione s : sottomissioni) {
            // Prendo la valutazione di ogni sottomissione dalla mappa
            Valutazione valutazione = mappaValutazioni.get(s.getId());

            // In questa fase, ogni sottomissione DEVE avere una valutazione (garantito da
            // terminaFaseValutazione, quindi teoricamente questo controllo non è
            // necessario, ma è comunque utile per la robustezza del codice)
            if (valutazione == null) {
                throw new IllegalStateException(
                        "Trovata sottomissione senza valutazione in fase di premiazione: " + s.getId());
            }

            classifica
                    .add(new ClassificaTeamDTO(s.getTeam().getId(), s.getTeam().getNomeTeam(), valutazione.getVoto()));
        }

        classifica.sort((t1, t2) -> Double.compare(t2.getPunteggio(), t1.getPunteggio()));

        return classifica;
    }

    public void proclamaVincitore(String hackathonId, String teamId, String organizzatoreId) {
        Hackathon hackathon = unitOfWork.hackathonRepository().findById(hackathonId)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon non trovato"));

        if (!hackathon.getOrganizzatore().getId().equals(organizzatoreId)) {
            throw new SecurityException("Solo l'organizzatore può proclamare il vincitore.");
        }

        if (hackathon.getStato() != StatoHackathon.IN_PREMIAZIONE) {
            throw new IllegalStateException("L'Hackathon non è in fase di premiazione.");
        }

        Team team = unitOfWork.teamRepository().findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team non trovato."));

        boolean haSottomesso = unitOfWork.sottomissioneRepository()
                .existsByPartecipazione_Hackathon_IdAndPartecipazione_Team_Id(hackathonId, teamId);
        if (!haSottomesso) {
            throw new IllegalArgumentException(
                    "Il team selezionato non è iscritto o non ha effettuato sottomissioni per questo Hackathon.");
        }

        // Process Payment (Il Process Payment, in una reale implementazione, potrebbe
        // lanciare eccezioni)
        try {
            paymentService.processPayment(team.getLeaderSquadra().getId(), hackathon.getPremioInDenaro());
        } catch (Exception e) {
            throw new IllegalStateException("Errore durante il pagamento: " + e.getMessage(), e);
        }

        hackathon.setVincitore(team);
        hackathon.setStato(StatoHackathon.CONCLUSO);
        unitOfWork.hackathonRepository().save(hackathon);
    }
}
