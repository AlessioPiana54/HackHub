package hackhub.app.Application.Services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hackhub.app.Application.IUnitOfWork.IUnitOfWork;
import hackhub.app.Application.Requests.CreaTeamRequest;
import hackhub.app.Core.Enums.Ruolo;
import hackhub.app.Core.Enums.StatoHackathon;
import hackhub.app.Core.POJO_Entities.Hackathon;
import hackhub.app.Core.POJO_Entities.Partecipazione;
import hackhub.app.Core.POJO_Entities.Team;
import java.util.List;
import hackhub.app.Core.POJO_Entities.User;

/**
 * Servizio per la gestione dei Team e delle relative operazioni.
 */
@Service
@Transactional
public class TeamService extends AbstractService {

    public TeamService(IUnitOfWork unitOfWork) {
        super(unitOfWork);
    }

    /**
     * Crea un nuovo Team e assegna l'utente richiedente come leader.
     *
     * @param request  i dati per la creazione del team (nome team)
     * @param leaderId l'ID dell'utente che crea il team
     * @return il Team creato
     * @throws IllegalArgumentException se l'utente non viene trovato o il nome del
     *                                  team esiste già
     * @throws SecurityException        se l'utente non ha il ruolo appropriato
     */
    public Team creaTeam(CreaTeamRequest request, String leaderId) {
        User new_leader = findUserOrThrow(leaderId);

        if (unitOfWork.teamRepository().existsByNomeTeam(request.getNomeTeam())) {
            throw new IllegalArgumentException("Esiste già un Team con questo nome.");
        }

        if (new_leader.getRuolo() != Ruolo.UTENTE_SENZA_TEAM) {
            throw new SecurityException("L'utente specificato non ha i permessi necessari.");
        }

        new_leader.setRuolo(Ruolo.LEADER_TEAM);
        unitOfWork.userRepository().save(new_leader);

        Team nuovoTeam = new Team(request.getNomeTeam(), new_leader);
        unitOfWork.teamRepository().save(nuovoTeam);

        return nuovoTeam;
    }

    /**
     * Iscrive un Team ad un Hackathon.
     *
     * @param teamId        l'ID del Team da iscrivere
     * @param hackathonId   l'ID dell'Hackathon a cui iscriversi
     * @param richiedenteId l'ID dell'utente che richiede l'iscrizione (deve essere
     *                      il leader)
     * @return l'oggetto Partecipazione creato
     * @throws IllegalArgumentException se team o hackathon non trovati, iscrizioni
     *                                  chiuse o team già iscritto
     * @throws SecurityException        se il richiedente non è il leader del team
     */
    public Partecipazione iscriviTeam(String teamId, String hackathonId, String richiedenteId) {
        Team team = findTeamOrThrow(teamId);

        if (team.getLeaderSquadra() == null || !team.getLeaderSquadra().getId().equals(richiedenteId)) {
            throw new SecurityException("Solo il Leader del Team può effettuare l'iscrizione.");
        }

        Hackathon hackathon = findHackathonOrThrow(hackathonId);

        if (hackathon.getStato() != StatoHackathon.IN_ISCRIZIONE) {
            throw new IllegalArgumentException("Le iscrizioni per questo Hackathon non sono aperte.");
        }

        // Verifica se il team è già impegnato in altre competizioni non ancora concluse
        List<Partecipazione> partecipazioni = unitOfWork.partecipazioneRepository().findByTeamId(teamId);
        for (Partecipazione p : partecipazioni) {
            if (p.getHackathon().getStato() != StatoHackathon.CONCLUSO) {
                throw new IllegalArgumentException("Il Team è già iscritto ad un Hackathon attivo.");
            }
        }

        Partecipazione p = new Partecipazione(team, hackathon);
        unitOfWork.partecipazioneRepository().save(p);
        return p;
    }

    /**
     * Permette ad un membro di abbandonare il Team.
     *
     * @param teamId   l'ID del Team da abbandonare
     * @param memberId l'ID del membro che vuole lasciare il team
     * @throws IllegalArgumentException se team o utente non trovati, o se l'utente
     *                                  non fa parte del team
     */
    public void abbandonaTeam(String teamId, String memberId) {
        Team team = findTeamOrThrow(teamId);
        User member = findUserOrThrow(memberId);

        // Check if the user is part of the team (throws SecurityException if not)
        validateUserInTeam(team, memberId, "L'utente non fa parte di questo Team.");

        // Check if the user is the leader
        if (team.getLeaderSquadra().getId().equals(memberId)) {
            throw new IllegalArgumentException("Il Leader deve cedere il ruolo prima di abbandonare il team.");
        }

        // Remove member from team
        team.getMembri().removeIf(m -> m.getId().equals(memberId));
        unitOfWork.teamRepository().save(team);

        // Update user role
        member.setRuolo(Ruolo.UTENTE_SENZA_TEAM);
        unitOfWork.userRepository().save(member);
    }
}
