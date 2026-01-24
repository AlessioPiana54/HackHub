package hackhub.app.Application.Services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import hackhub.app.Application.IRepositories.IPartecipazioneRepository;
import hackhub.app.Application.IRepositories.IRichiestaSupportoRepository;
import hackhub.app.Application.IRepositories.IUserRepository;
import hackhub.app.Application.Requests.CreaRichiestaSupportoRequest;
import hackhub.app.Core.POJO_Entities.Partecipazione;
import hackhub.app.Core.POJO_Entities.RichiestaSupporto;
import hackhub.app.Core.POJO_Entities.User;

@Service
@Transactional
public class RichiestaSupportoService {
    private final IRichiestaSupportoRepository richiestaSupportoRepo;
    private final IPartecipazioneRepository partecipazioneRepo;
    private final IUserRepository userRepo;

    @Autowired
    public RichiestaSupportoService(IRichiestaSupportoRepository richiestaSupportoRepo,
            IPartecipazioneRepository partecipazioneRepo,
            IUserRepository userRepo) {
        this.richiestaSupportoRepo = richiestaSupportoRepo;
        this.partecipazioneRepo = partecipazioneRepo;
        this.userRepo = userRepo;
    }

    public RichiestaSupporto creaRichiesta(CreaRichiestaSupportoRequest request) {
        Partecipazione partecipazione = partecipazioneRepo
                .findByTeamIdAndHackathonId(request.getTeamId(), request.getHackathonId())
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Nessuna partecipazione trovata per il team e hackathon specificati."));

        User richiedente = userRepo.findById(request.getRichiedenteId())
                .orElseThrow(() -> new IllegalArgumentException("Utente richiedente non trovato."));

        boolean isLeader = partecipazione.getTeam().getLeaderSquadra().getId().equals(request.getRichiedenteId());
        boolean isMembro = partecipazione.getTeam().getMembri().stream()
                .anyMatch(m -> m.getId().equals(request.getRichiedenteId()));

        if (!isLeader && !isMembro) {
            throw new SecurityException("L'utente richiedente non appartiene al team partecipante.");
        }

        RichiestaSupporto nuovaRichiesta = new RichiestaSupporto(partecipazione, richiedente, request.getDescrizione());
        richiestaSupportoRepo.save(nuovaRichiesta);

        return nuovaRichiesta;
    }
}
