package hackhub.app.Application.Services;

import hackhub.app.Application.IUnitOfWork.IUnitOfWork;
import hackhub.app.Core.POJO_Entities.Hackathon;
import hackhub.app.Core.POJO_Entities.Partecipazione;
import hackhub.app.Core.POJO_Entities.Team;
import hackhub.app.Core.POJO_Entities.User;
import hackhub.app.Core.POJO_Entities.Sottomissione;

/**
 * Classe astratta base per i servizi dell'applicazione.
 * Fornisce l'accesso all'Unit of Work e metodi di utilità comuni per il
 * recupero delle entità.
 */
public abstract class AbstractService {

    protected final IUnitOfWork unitOfWork;

    public AbstractService(IUnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    /**
     * Recupera un utente dal repository o lancia un'eccezione se non trovato.
     *
     * @param id l'ID dell'utente
     * @return l'entità User trovata
     * @throws IllegalArgumentException se l'utente non viene trovato
     */
    protected User findUserOrThrow(String id) {
        return unitOfWork.userRepository().findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato nel database: " + id));
    }

    /**
     * Recupera un Hackathon dal repository o lancia un'eccezione se non trovato.
     *
     * @param id l'ID dell'Hackathon
     * @return l'entità Hackathon trovata
     * @throws IllegalArgumentException se l'hackathon non viene trovato
     */
    protected Hackathon findHackathonOrThrow(String id) {
        return unitOfWork.hackathonRepository().findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon non trovato: " + id));
    }

    /**
     * Recupera un Team dal repository o lancia un'eccezione se non trovato.
     *
     * @param id l'ID del Team
     * @return l'entità Team trovata
     * @throws IllegalArgumentException se il team non viene trovato
     */
    protected Team findTeamOrThrow(String id) {
        return unitOfWork.teamRepository().findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Team non trovato: " + id));
    }

    /**
     * Recupera una Partecipazione dal repository o lancia un'eccezione se non
     * trovata.
     *
     * @param teamId      l'ID del Team
     * @param hackathonId l'ID dell'Hackathon
     * @return l'entità Partecipazione trovata
     * @throws IllegalArgumentException se la partecipazione non viene trovata
     */
    protected Partecipazione findPartecipazioneOrThrow(String teamId, String hackathonId) {
        return unitOfWork.partecipazioneRepository().findByTeamIdAndHackathonId(teamId, hackathonId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Partecipazione non trovata per il team e hackathon specificati."));
    }

    /**
     * Recupera una Sottomissione dal repository o lancia un'eccezione se non
     * trovata.
     *
     * @param id l'ID della sottomissione
     * @return l'entità Sottomissione trovata
     * @throws IllegalArgumentException se la sottomissione non viene trovata
     */
    protected Sottomissione findSottomissioneOrThrow(String id) {
        return unitOfWork.sottomissioneRepository().findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sottomissione non trovata: " + id));
    }

    /**
     * Valida che un utente abbia il ruolo specificato.
     *
     * @param user         l'utente da controllare
     * @param expectedRole il ruolo atteso
     * @param errorMessage il messaggio di errore da lanciare se il ruolo non
     *                     corrisponde
     * @throws IllegalArgumentException se il ruolo non corrisponde (nota: usa
     *                                  IllegalArgument per compatibilità con codice
     *                                  esistente)
     */
    protected void validateUserRole(User user, hackhub.app.Core.Enums.Ruolo expectedRole, String errorMessage) {
        if (user.getRuolo() != expectedRole) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    /**
     * Valida che un utente faccia parte di un team (come Leader o Membro).
     *
     * @param team         il Team da controllare
     * @param userId       l'ID dell'utente
     * @param errorMessage il messaggio di errore da lanciare se l'utente non è nel
     *                     team
     * @throws SecurityException se l'utente non fa parte del team
     */
    protected void validateUserInTeam(Team team, String userId, String errorMessage) {
        boolean isLeader = team.getLeaderSquadra().getId().equals(userId);
        boolean isMembro = team.getMembri().stream().anyMatch(m -> m.getId().equals(userId));

        if (!isLeader && !isMembro) {
            throw new SecurityException(errorMessage);
        }
    }

    /**
     * Valida che un utente sia un mentore per un determinato Hackathon.
     *
     * @param hackathon        l'Hackathon da controllare
     * @param possibleMentorId l'ID dell'utente da controllare
     * @param errorMessage     il messaggio di errore da lanciare se l'utente non è
     *                         un mentore per l'hackathon
     * @throws SecurityException se l'utente non è un mentore per l'hackathon
     */
    protected void validateUserIsMentorInHackathon(Hackathon hackathon, String possibleMentorId, String errorMessage) {
        boolean isMentor = hackathon.getMentori().stream()
                .anyMatch(m -> m.getId().equals(possibleMentorId));

        if (!isMentor) {
            throw new SecurityException(errorMessage);
        }
    }
}
