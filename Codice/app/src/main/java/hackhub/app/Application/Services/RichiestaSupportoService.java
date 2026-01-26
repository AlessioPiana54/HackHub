package hackhub.app.Application.Services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import hackhub.app.Application.IRepositories.IPartecipazioneRepository;
import hackhub.app.Application.IRepositories.IRichiestaSupportoRepository;
import hackhub.app.Application.IRepositories.IUserRepository;
import hackhub.app.Application.Requests.CreaRichiestaSupportoRequest;
import hackhub.app.Application.Requests.ProponiCallRequest;
import hackhub.app.Core.POJO_Entities.Partecipazione;
import hackhub.app.Core.POJO_Entities.RichiestaSupporto;
import hackhub.app.Core.POJO_Entities.User;
import hackhub.app.Core.POJO_Entities.Hackathon;
import hackhub.app.Application.IRepositories.IHackathonRepository;
import hackhub.app.Application.IRepositories.ITeamRepository;
import hackhub.app.Core.POJO_Entities.Team;
import java.util.List;
import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class RichiestaSupportoService {
        private final IRichiestaSupportoRepository richiestaSupportoRepo;
        private final IPartecipazioneRepository partecipazioneRepo;
        private final IUserRepository userRepo;
        private final IHackathonRepository hackathonRepo;
        private final ITeamRepository teamRepo;

        @Autowired
        public RichiestaSupportoService(IRichiestaSupportoRepository richiestaSupportoRepo,
                        IPartecipazioneRepository partecipazioneRepo,
                        IUserRepository userRepo,
                        IHackathonRepository hackathonRepo,
                        ITeamRepository teamRepo) {
                this.richiestaSupportoRepo = richiestaSupportoRepo;
                this.partecipazioneRepo = partecipazioneRepo;
                this.userRepo = userRepo;
                this.hackathonRepo = hackathonRepo;
                this.teamRepo = teamRepo;
        }

        public RichiestaSupporto creaRichiesta(CreaRichiestaSupportoRequest request) {
                Partecipazione partecipazione = partecipazioneRepo
                                .findByTeamIdAndHackathonId(request.getTeamId(), request.getHackathonId())
                                .orElseThrow(
                                                () -> new IllegalArgumentException(
                                                                "Nessuna partecipazione trovata per il team e hackathon specificati."));

                User richiedente = userRepo.findById(request.getRichiedenteId())
                                .orElseThrow(() -> new IllegalArgumentException("Utente richiedente non trovato."));

                boolean isLeader = partecipazione.getTeam().getLeaderSquadra().getId()
                                .equals(request.getRichiedenteId());
                boolean isMembro = partecipazione.getTeam().getMembri().stream()
                                .anyMatch(m -> m.getId().equals(request.getRichiedenteId()));

                if (!isLeader && !isMembro) {
                        throw new SecurityException("L'utente richiedente non appartiene al team partecipante.");
                }

                RichiestaSupporto nuovaRichiesta = new RichiestaSupporto(partecipazione, richiedente,
                                request.getDescrizione());
                richiestaSupportoRepo.save(nuovaRichiesta);

                return nuovaRichiesta;
        }

        public List<RichiestaSupporto> getRichiestePerMentore(String hackathonId, String mentorId) {
                Hackathon hackathon = hackathonRepo.findById(hackathonId)
                                .orElseThrow(() -> new IllegalArgumentException("Hackathon non trovato"));

                boolean isMentor = hackathon.getMentori().stream()
                                .anyMatch(m -> m.getId().equals(mentorId));

                if (!isMentor) {
                        throw new SecurityException("L'utente non è un mentore per questo Hackathon");
                }

                return richiestaSupportoRepo.findByPartecipazioneHackathonId(hackathonId);
        }

        public RichiestaSupporto proponiCall(ProponiCallRequest request) {
                RichiestaSupporto richiesta = richiestaSupportoRepo.findById(request.getRichiestaId())
                                .orElseThrow(() -> new IllegalArgumentException("Richiesta di supporto non trovata"));

                Hackathon hackathon = richiesta.getHackathon();

                boolean isMentor = hackathon.getMentori().stream()
                                .anyMatch(m -> m.getId().equals(request.getMentorId()));

                if (!isMentor) {
                        throw new SecurityException(
                                        "L'utente non è un mentore per questo Hackathon e non può gestire la richiesta");
                }

                richiesta.setLinkCall(request.getLinkCall());
                richiesta.setDataCall(request.getDataCall());
                return richiestaSupportoRepo.save(richiesta);
        }

        public List<RichiestaSupporto> getRichiesteGestitePerTeam(String hackathonId, String teamId, String userId) {
                Team team = teamRepo.findById(teamId)
                                .orElseThrow(() -> new IllegalArgumentException("Team non trovato."));

                boolean isLeader = team.getLeaderSquadra().getId().equals(userId);
                boolean isMembro = team.getMembri().stream().anyMatch(m -> m.getId().equals(userId));

                if (!isLeader && !isMembro) {
                        throw new SecurityException("L'utente non appartiene al team specificato.");
                }

                partecipazioneRepo.findByTeamIdAndHackathonId(teamId, hackathonId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Il team non partecipa a questo Hackathon."));

                return richiestaSupportoRepo.findByPartecipazioneHackathonIdAndPartecipazioneTeamId(
                                hackathonId,
                                teamId).stream()
                                .filter(r -> r.getDataCall() != null && r.getLinkCall() != null
                                                && !r.getLinkCall().isEmpty())
                                .collect(toList());
        }
}
