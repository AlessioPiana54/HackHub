package hackhub.app.Application.Services;

import hackhub.app.Core.POJO_Entities.Hackathon;
import hackhub.app.Core.POJO_Entities.Team;
import hackhub.app.Core.POJO_Entities.User;
import org.springframework.stereotype.Component;

/**
 * Componente responsabile della validazione delle autorizzazioni.
 * Centralizza la logica di controllo dei permessi degli utenti.
 */
@Component
public class AuthorizationChecker {

    /**
     * Valida che un utente abbia il ruolo specificato.
     *
     * @param user         l'utente da controllare
     * @param expectedRole il ruolo atteso
     * @param errorMessage il messaggio di errore da lanciare se il ruolo non
     *                     corrisponde
     * @throws SecurityException se il ruolo non corrisponde
     */
    public void validateUserRole(
        User user,
        hackhub.app.Core.Enums.Ruolo expectedRole,
        String errorMessage
    ) {
        if (user.getRuolo() != expectedRole) {
            throw new SecurityException(errorMessage);
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
    public void validateUserInTeam(
        Team team,
        String userId,
        String errorMessage
    ) {
        boolean isLeader = team.getLeaderSquadra().getId().equals(userId);
        boolean isMembro = team
            .getMembri()
            .stream()
            .anyMatch(m -> m.getId().equals(userId));

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
    public void validateUserIsMentorInHackathon(
        Hackathon hackathon,
        String possibleMentorId,
        String errorMessage
    ) {
        boolean isMentor = hackathon
            .getMentori()
            .stream()
            .anyMatch(m -> m.getId().equals(possibleMentorId));

        if (!isMentor) {
            throw new SecurityException(errorMessage);
        }
    }
}
