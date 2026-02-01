package hackhub.app.Application.Services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hackhub.app.Application.IUnitOfWork.IUnitOfWork;
import hackhub.app.Application.Requests.CreaSegnalazioneRequest;
import hackhub.app.Core.POJO_Entities.Hackathon;
import hackhub.app.Core.POJO_Entities.Partecipazione;
import hackhub.app.Core.POJO_Entities.Segnalazione;
import hackhub.app.Core.POJO_Entities.User;
import hackhub.app.Application.DTOs.SegnalazioneDTO;
import java.util.List;
import static java.util.stream.Collectors.toList;

/**
 * Servizio per la gestione delle segnalazioni da parte dei mentori.
 */
@Service
@Transactional
public class SegnalazioneService extends AbstractService {

        public SegnalazioneService(IUnitOfWork unitOfWork) {
                super(unitOfWork);
        }

        /**
         * Crea una nuova segnalazione per un team da parte di un mentore.
         *
         * @param request   i dati della segnalazione
         * @param mentoreId l'ID del mentore che crea la segnalazione
         * @return la Segnalazione creata
         * @throws IllegalArgumentException se mentore, team o hackathon non trovati, o
         *                                  il team non partecipa
         * @throws SecurityException        se l'utente non è un mentore per l'hackathon
         */
        public Segnalazione creaSegnalazione(CreaSegnalazioneRequest request, String mentoreId) {
                User mentore = findUserOrThrow(mentoreId);
                Partecipazione partecipazione = findPartecipazioneOrThrow(request.getIdTeam(),
                                request.getIdHackathon());

                Hackathon hackathon = partecipazione.getHackathon();
                validateUserIsMentorInHackathon(hackathon, mentoreId,
                                "L'utente " + mentore.getNome() + " non è un mentore per questo Hackathon.");

                Segnalazione segnalazione = new Segnalazione(partecipazione, mentore, request.getDescrizione());
                unitOfWork.segnalazioneRepository().save(segnalazione);
                return segnalazione;
        }

        /**
         * Restituisce la lista delle segnalazioni per un Hackathon (chiamata
         * dall'organizzatore).
         *
         * @param idHackathon l'ID dell'Hackathon
         * @param idOrganizer l'ID dell'organizzatore richiedente
         * @return la lista di SegnalazioneDTO
         * @throws IllegalArgumentException se l'hackathon non viene trovato
         * @throws SecurityException        se il richiedente non è l'organizzatore
         */
        public List<SegnalazioneDTO> getSegnalazioni(String idHackathon, String idOrganizer) {
                Hackathon h = findHackathonOrThrow(idHackathon);

                if (!h.getOrganizzatore().getId().equals(idOrganizer)) {
                        throw new SecurityException("Solo l'organizzatore può vedere le segnalazioni.");
                }

                // Recupera le segnalazioni relative all'hackathon e le mappa in DTO
                return unitOfWork.segnalazioneRepository().findByPartecipazioneHackathonId(idHackathon).stream()
                                .map(this::mapToDTO)
                                .collect(toList());
        }

        // --- Private Helper Methods ---

        private SegnalazioneDTO mapToDTO(Segnalazione s) {
                return new SegnalazioneDTO(
                                s.getId(),
                                s.getHackathon().getId(),
                                s.getHackathon().getNome(),
                                s.getTeam().getId(),
                                s.getTeam().getNomeTeam(),
                                s.getMentore().getId(),
                                s.getMentore().getNome(),
                                s.getDescrizione(),
                                s.getDataSegnalazione());
        }
}
