package hackhub.app.Application.Services;

import hackhub.app.Application.IUnitOfWork.IUnitOfWork;
import hackhub.app.Core.POJO_Entities.Hackathon;
import hackhub.app.Core.POJO_Entities.Partecipazione;
import hackhub.app.Core.POJO_Entities.Sottomissione;
import hackhub.app.Core.POJO_Entities.Team;
import hackhub.app.Core.POJO_Entities.User;
import org.springframework.stereotype.Component;

/**
 * Componente responsabile del recupero delle entità dai repository.
 * Centralizza la logica di find-or-throw per tutte le entità principali.
 */
@Component
public class EntityFinder {

    private final IUnitOfWork unitOfWork;

    public EntityFinder(IUnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    /**
     * Recupera un utente dal repository o lancia un'eccezione se non trovato.
     *
     * @param id l'ID dell'utente
     * @return l'entità User trovata
     * @throws IllegalArgumentException se l'utente non viene trovato
     */
    public User findUserOrThrow(String id) {
        return unitOfWork
            .userRepository()
            .findById(id)
            .orElseThrow(() ->
                new IllegalArgumentException("Utente non trovato nel database: " + id)
            );
    }

    /**
     * Recupera un Hackathon dal repository o lancia un'eccezione se non trovato.
     *
     * @param id l'ID dell'Hackathon
     * @return l'entità Hackathon trovata
     * @throws IllegalArgumentException se l'hackathon non viene trovato
     */
    public Hackathon findHackathonOrThrow(String id) {
        return unitOfWork
            .hackathonRepository()
            .findByIdString(id)
            .orElseThrow(() ->
                new IllegalArgumentException("Hackathon non trovato: " + id)
            );
    }

    /**
     * Recupera un Team dal repository o lancia un'eccezione se non trovato.
     *
     * @param id l'ID del Team
     * @return l'entità Team trovata
     * @throws IllegalArgumentException se il team non viene trovato
     */
    public Team findTeamOrThrow(String id) {
        return unitOfWork
            .teamRepository()
            .findByIdString(id)
            .orElseThrow(() -> new IllegalArgumentException("Team non trovato: " + id)
            );
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
    public Partecipazione findPartecipazioneOrThrow(
        String teamId,
        String hackathonId
    ) {
        return unitOfWork
            .partecipazioneRepository()
            .findByTeamIdAndHackathonId(teamId, hackathonId)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "Partecipazione non trovata per il team e hackathon specificati."
                )
            );
    }

    /**
     * Recupera una Sottomissione dal repository o lancia un'eccezione se non
     * trovata.
     *
     * @param id l'ID della sottomissione
     * @return l'entità Sottomissione trovata
     * @throws IllegalArgumentException se la sottomissione non viene trovata
     */
    public Sottomissione findSottomissioneOrThrow(String id) {
        return unitOfWork
            .sottomissioneRepository()
            .findById(id)
            .orElseThrow(() ->
                new IllegalArgumentException("Sottomissione non trovata: " + id)
            );
    }
}
