package hackhub.app.Application.Services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import hackhub.app.Application.IUnitOfWork.IUnitOfWork;
import hackhub.app.Application.Requests.CreaSegnalazioneRequest;
import hackhub.app.Core.POJO_Entities.Hackathon;
import hackhub.app.Core.POJO_Entities.Partecipazione;
import hackhub.app.Core.POJO_Entities.Segnalazione;
import hackhub.app.Core.POJO_Entities.User;
import java.util.List;

@Service
@Transactional
public class SegnalazioneService {
        private final IUnitOfWork unitOfWork;

        @Autowired
        public SegnalazioneService(IUnitOfWork unitOfWork) {
                this.unitOfWork = unitOfWork;
        }

        public Segnalazione creaSegnalazione(CreaSegnalazioneRequest request, String mentoreId) {
                User mentore = unitOfWork.userRepository().findById(mentoreId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Mentore non trovato: " + mentoreId));
                Partecipazione partecipazione = unitOfWork.partecipazioneRepository()
                                .findByTeamIdAndHackathonId(request.getIdTeam(), request.getIdHackathon())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Il Team indicato non partecipa all'Hackathon specificato."));
                Hackathon hackathon = partecipazione.getHackathon();
                boolean isMentor = hackathon.getMentori().stream()
                                .anyMatch(m -> m.getId().equals(mentore.getId()));
                if (!isMentor) {
                        throw new SecurityException(
                                        "L'utente " + mentore.getNome() + " non è un mentore per questo Hackathon.");
                }
                Segnalazione segnalazione = new Segnalazione(partecipazione, mentore, request.getDescrizione());
                unitOfWork.segnalazioneRepository().save(segnalazione);
                return segnalazione;
        }

        public List<Segnalazione> getSegnalazioni(String idHackathon, String idOrganizer) {
                Hackathon h = unitOfWork.hackathonRepository().findById(idHackathon)
                                .orElseThrow(() -> new IllegalArgumentException("Hackathon non trovato"));
                if (!h.getOrganizzatore().getId().equals(idOrganizer)) {
                        throw new SecurityException("Solo l'organizzatore può vedere le segnalazioni.");
                }
                return unitOfWork.segnalazioneRepository().findByPartecipazioneHackathonId(idHackathon);
        }
}
