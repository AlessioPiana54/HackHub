package hackhub.app.Application.Services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import hackhub.app.Application.IUnitOfWork.IUnitOfWork;
import hackhub.app.Application.Requests.CreaRichiestaSupportoRequest;
import hackhub.app.Application.Requests.ProponiCallRequest;
import hackhub.app.Application.Strategies.LinkStrategyContext;
import hackhub.app.Core.POJO_Entities.Partecipazione;
import hackhub.app.Core.POJO_Entities.RichiestaSupporto;
import hackhub.app.Core.POJO_Entities.User;
import hackhub.app.Core.POJO_Entities.Hackathon;
import hackhub.app.Core.POJO_Entities.Team;
import java.util.List;
import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class RichiestaSupportoService {
        private final IUnitOfWork unitOfWork;
        private final LinkStrategyContext linkStrategyContext;

        @Autowired
        public RichiestaSupportoService(IUnitOfWork unitOfWork,
                        LinkStrategyContext linkStrategyContext) {
                this.unitOfWork = unitOfWork;
                this.linkStrategyContext = linkStrategyContext;
        }

        public RichiestaSupporto creaRichiesta(CreaRichiestaSupportoRequest request) {
                Partecipazione partecipazione = unitOfWork.partecipazioneRepository()
                                .findByTeamIdAndHackathonId(request.getTeamId(), request.getHackathonId())
                                .orElseThrow(
                                                () -> new IllegalArgumentException(
                                                                "Nessuna partecipazione trovata per il team e hackathon specificati."));

                User richiedente = unitOfWork.userRepository().findById(request.getRichiedenteId())
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
                unitOfWork.richiestaSupportoRepository().save(nuovaRichiesta);

                return nuovaRichiesta;
        }

        public List<RichiestaSupporto> getRichiestePerMentore(String hackathonId, String mentorId) {
                Hackathon hackathon = unitOfWork.hackathonRepository().findById(hackathonId)
                                .orElseThrow(() -> new IllegalArgumentException("Hackathon non trovato"));

                boolean isMentor = hackathon.getMentori().stream()
                                .anyMatch(m -> m.getId().equals(mentorId));

                if (!isMentor) {
                        throw new SecurityException("L'utente non è un mentore per questo Hackathon");
                }

                return unitOfWork.richiestaSupportoRepository().findByPartecipazioneHackathonId(hackathonId);
        }

        public RichiestaSupporto proponiCall(ProponiCallRequest request) {
                if (!linkStrategyContext.validate(request.getLinkCall(), java.util.List.of("Google Meet", "Webex"))) {
                        throw new IllegalArgumentException("Il link della call deve essere di Google Meet o Webex.");
                }

                RichiestaSupporto richiesta = unitOfWork.richiestaSupportoRepository()
                                .findById(request.getRichiestaId())
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
                return unitOfWork.richiestaSupportoRepository().save(richiesta);
        }

        public List<RichiestaSupporto> getRichiesteGestitePerTeam(String hackathonId, String teamId, String userId) {
                Team team = unitOfWork.teamRepository().findById(teamId)
                                .orElseThrow(() -> new IllegalArgumentException("Team non trovato."));

                boolean isLeader = team.getLeaderSquadra().getId().equals(userId);
                boolean isMembro = team.getMembri().stream().anyMatch(m -> m.getId().equals(userId));

                if (!isLeader && !isMembro) {
                        throw new SecurityException("L'utente non appartiene al team specificato.");
                }

                unitOfWork.partecipazioneRepository().findByTeamIdAndHackathonId(teamId, hackathonId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Il team non partecipa a questo Hackathon."));

                return unitOfWork.richiestaSupportoRepository().findByPartecipazioneHackathonIdAndPartecipazioneTeamId(
                                hackathonId,
                                teamId).stream()
                                .filter(r -> r.getDataCall() != null && r.getLinkCall() != null
                                                && !r.getLinkCall().isEmpty())
                                .collect(toList());
        }
}
