package hackhub.app.Application.Services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import hackhub.app.Application.IRepositories.IHackathonRepository;
import hackhub.app.Application.IRepositories.IPartecipazioneRepository;
import hackhub.app.Application.IRepositories.ISegnalazioneRepository;
import hackhub.app.Application.IRepositories.IUserRepository;
import hackhub.app.Application.Requests.CreaSegnalazioneRequest;
import hackhub.app.Core.POJO_Entities.Hackathon;
import hackhub.app.Core.POJO_Entities.Partecipazione;
import hackhub.app.Core.POJO_Entities.Segnalazione;
import hackhub.app.Core.POJO_Entities.User;
import java.util.List;

@Service
@Transactional
public class SegnalazioneService {
        private final ISegnalazioneRepository segnalazioneRepo;
        private final IHackathonRepository hackathonRepo;
        private final IUserRepository userRepo;
        private final IPartecipazioneRepository partecipazioneRepo;

        @Autowired
        public SegnalazioneService(ISegnalazioneRepository segnalazioneRepo,
                        IHackathonRepository hackathonRepo,
                        IUserRepository userRepo,
                        IPartecipazioneRepository partecipazioneRepo) {
                this.segnalazioneRepo = segnalazioneRepo;
                this.hackathonRepo = hackathonRepo;
                this.userRepo = userRepo;
                this.partecipazioneRepo = partecipazioneRepo;
        }

        public Segnalazione creaSegnalazione(CreaSegnalazioneRequest request) {
                User mentore = userRepo.findById(request.getIdMentore())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Mentore non trovato: " + request.getIdMentore()));
                Partecipazione partecipazione = partecipazioneRepo
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
                segnalazioneRepo.save(segnalazione);
                return segnalazione;
        }

        public List<Segnalazione> getSegnalazioni(String idHackathon, String idOrganizer) {
                Hackathon h = hackathonRepo.findById(idHackathon)
                                .orElseThrow(() -> new IllegalArgumentException("Hackathon non trovato"));
                if (!h.getOrganizzatore().getId().equals(idOrganizer)) {
                        throw new SecurityException("Solo l'organizzatore può vedere le segnalazioni.");
                }
                return segnalazioneRepo.findByHackathonId(idHackathon);
        }
}
