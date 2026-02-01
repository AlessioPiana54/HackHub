package hackhub.app.Application.Services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hackhub.app.Application.DTOs.ClassificaTeamDTO;
import hackhub.app.Application.DTOs.HackathonSummaryDTO;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servizio per la gestione degli Hackathon.
 * Gestisce il ciclo di vita degli Hackathon (creazione, valutazione,
 * premiazione).
 */
@Service
@Transactional
public class HackathonService extends AbstractService {

    private final IPaymentManager paymentService;

    public HackathonService(IUnitOfWork unitOfWork, IPaymentManager paymentService) {
        super(unitOfWork);
        this.paymentService = paymentService;
    }

    /**
     * Crea un nuovo Hackathon.
     *
     * @param request         i dati per la creazione dell'hackathon
     * @param organizzatoreId l'ID dell'organizzatore che crea l'evento
     * @return l'Hackathon creato
     * @throws IllegalArgumentException se gli utenti (organizzatore, giudice,
     *                                  mentori) non vengono trovati o non hanno i
     *                                  ruoli corretti
     */
    public Hackathon creaHackathon(CreaHackathonRequest request, String organizzatoreId) {
        User organizzatore = findUserOrThrow(organizzatoreId);
        User giudice = findUserOrThrow(request.getIdGiudice());

        validateUserRole(organizzatore, Ruolo.ORGANIZZATORE,
                "L'utente specificato come organizzatore non ha i permessi necessari.");
        validateUserRole(giudice, Ruolo.GIUDICE, "L'utente specificato come giudice non ha il ruolo di GIUDICE.");

        List<User> listaMentori = unitOfWork.userRepository().findAllById(request.getIdMentori());
        if (listaMentori.size() != request.getIdMentori().size()) {
            throw new IllegalArgumentException("Uno o più mentori non trovati.");
        }
        for (User mentore : listaMentori) {
            validateUserRole(mentore, Ruolo.MENTORE, "L'utente " + mentore.getNome() + " non ha il ruolo di MENTORE.");
        }

        // Calcolo dello Stato iniziale dell'Hackathon
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

    /**
     * Termina la fase di valutazione di un Hackathon.
     *
     * @param hackathonId l'ID dell'Hackathon
     * @param giudiceId   l'ID del giudice che esegue l'operazione
     * @throws IllegalStateException se l'hackathon non è in valutazione o se ci
     *                               sono sottomissioni non valutate
     * @throws SecurityException     se l'utente non è il giudice dell'hackathon
     */
    public void terminaFaseValutazione(String hackathonId, String giudiceId) {
        Hackathon hackathon = findHackathonOrThrow(hackathonId);

        if (hackathon.getStato() != StatoHackathon.IN_VALUTAZIONE) {
            throw new IllegalStateException("L'Hackathon non è in fase di valutazione");
        }

        if (!hackathon.getGiudice().getId().equals(giudiceId)) {
            throw new SecurityException("Solo il giudice dell'Hackathon può terminare la valutazione");
        }

        // Recupera gli ID di tutte le sottomissioni già valutate per questo hackathon
        Set<String> sottomissioniValutateIds = unitOfWork.valutazioneRepository()
                .findAllBySottomissione_Partecipazione_Hackathon_Id(hackathonId).stream()
                .map(v -> v.getSottomissione().getId())
                .collect(Collectors.toSet());

        // Verifica se esiste almeno una sottomissione non ancora valutata per questo
        // hackathon
        if (unitOfWork.sottomissioneRepository().findByPartecipazioneHackathonId(hackathonId).stream()
                .anyMatch(s -> !sottomissioniValutateIds.contains(s.getId()))) {
            throw new IllegalStateException(
                    "Non tutte le sottomissioni sono state valutate. Impossibile terminare la fase di valutazione.");
        }

        hackathon.setStato(StatoHackathon.IN_PREMIAZIONE);
        unitOfWork.hackathonRepository().save(hackathon);
    }

    /**
     * Restituisce la classifica dei team per un dato Hackathon.
     *
     * @param hackathonId l'ID dell'Hackathon
     * @param requesterId l'ID dell'utente che richiede la classifica
     * @return la lista ordinata dei team con i relativi punteggi
     * @throws IllegalStateException se l'hackathon non è in fase di premiazione o
     *                               concluso
     * @throws SecurityException     se il richiedente non ha i permessi necessari
     */
    public List<ClassificaTeamDTO> getClassifica(String hackathonId, String requesterId) {
        Hackathon hackathon = findHackathonOrThrow(hackathonId);

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

        // Recupera tutte le sottomissioni e le valutazioni per l'hackathon
        List<Sottomissione> sottomissioni = unitOfWork.sottomissioneRepository()
                .findByPartecipazioneHackathonId(hackathonId);

        // Mappa le valutazioni per ID sottomissione per un accesso rapido
        Map<String, Valutazione> mappaValutazioni = unitOfWork.valutazioneRepository()
                .findAllBySottomissione_Partecipazione_Hackathon_Id(hackathonId).stream()
                .collect(Collectors.toMap(v -> v.getSottomissione().getId(), v -> v));

        // Costruisce la classifica abbinando sottomissioni e valutazioni, ordinando per
        // punteggio decrescente
        return sottomissioni.stream()
                .map(s -> {
                    Valutazione valutazione = mappaValutazioni.get(s.getId());
                    if (valutazione == null) {
                        throw new IllegalStateException(
                                "Trovata sottomissione senza valutazione in fase di premiazione: " + s.getId());
                    }
                    return mapToClassificaTeamDTO(s, valutazione);
                })
                .sorted((t1, t2) -> Double.compare(t2.getPunteggio(), t1.getPunteggio()))
                .collect(Collectors.toList());
    }

    /**
     * Proclama un team vincitore dell'Hackathon ed effettua il pagamento del
     * premio.
     *
     * @param hackathonId     l'ID dell'Hackathon
     * @param teamId          l'ID del Team vincitore
     * @param organizzatoreId l'ID dell'organizzatore
     * @throws IllegalStateException se l'hackathon non è in premiazione o fallisce
     *                               il pagamento
     * @throws SecurityException     se il richiedente non è l'organizzatore
     */
    public void proclamaVincitore(String hackathonId, String teamId, String organizzatoreId) {
        Hackathon hackathon = findHackathonOrThrow(hackathonId);

        if (!hackathon.getOrganizzatore().getId().equals(organizzatoreId)) {
            throw new SecurityException("Solo l'organizzatore può proclamare il vincitore.");
        }

        if (hackathon.getStato() != StatoHackathon.IN_PREMIAZIONE) {
            throw new IllegalStateException("L'Hackathon non è in fase di premiazione.");
        }

        Team team = findTeamOrThrow(teamId);

        boolean haSottomesso = unitOfWork.sottomissioneRepository()
                .existsByPartecipazione_Hackathon_IdAndPartecipazione_Team_Id(hackathonId, teamId);
        if (!haSottomesso) {
            throw new IllegalArgumentException(
                    "Il team selezionato non è iscritto o non ha effettuato sottomissioni per questo Hackathon.");
        }

        try {
            paymentService.processPayment(team.getLeaderSquadra().getId(), hackathon.getPremioInDenaro());
        } catch (Exception e) {
            throw new IllegalStateException("Errore durante il pagamento: " + e.getMessage(), e);
        }

        hackathon.setVincitore(team);
        hackathon.setStato(StatoHackathon.CONCLUSO);
        unitOfWork.hackathonRepository().save(hackathon);
    }

    /**
     * Restituisce una lista di riepilogo di tutti gli Hackathon pubblici.
     *
     * @return la lista di HackathonSummaryDTO
     */
    public List<HackathonSummaryDTO> getPublicHackathons() {
        // Recupera tutti gli hackathon dal DB e li trasforma in DTO sintetici per la
        // visualizzazione pubblica
        return unitOfWork.hackathonRepository().findAll().stream()
                .map(this::mapToHackathonSummaryDTO)
                .collect(Collectors.toList());
    }

    // --- Helper private methods ---

    private ClassificaTeamDTO mapToClassificaTeamDTO(Sottomissione s, Valutazione v) {
        return new ClassificaTeamDTO(s.getTeam().getId(), s.getTeam().getNomeTeam(), v.getVoto());
    }

    private HackathonSummaryDTO mapToHackathonSummaryDTO(Hackathon h) {
        return new HackathonSummaryDTO(
                h.getNome(),
                h.getRegolamento(),
                h.getDataInizio(),
                h.getDataFine(),
                h.getLuogo(),
                h.getPremioInDenaro(),
                h.getStato(),
                h.getOrganizzatore().getNome());
    }
}
