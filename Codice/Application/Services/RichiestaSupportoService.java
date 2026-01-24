package Application.Services;

import Application.IRepositories.IPartecipazioneRepository;
import Application.IRepositories.IRichiestaSupportoRepository;
import Application.IRepositories.IUserRepository;
import Application.Requests.CreaRichiestaSupportoRequest;
import Core.POJO_Entities.Partecipazione;
import Core.POJO_Entities.RichiestaSupporto;
import Core.POJO_Entities.User;

public class RichiestaSupportoService {
    private final IRichiestaSupportoRepository richiestaSupportoRepo;
    private final IPartecipazioneRepository partecipazioneRepo;
    private final IUserRepository userRepo;

    public RichiestaSupportoService(IRichiestaSupportoRepository richiestaSupportoRepo,
            IPartecipazioneRepository partecipazioneRepo,
            IUserRepository userRepo) {
        this.richiestaSupportoRepo = richiestaSupportoRepo;
        this.partecipazioneRepo = partecipazioneRepo;
        this.userRepo = userRepo;
    }

    public RichiestaSupporto creaRichiesta(CreaRichiestaSupportoRequest request) throws Exception {
        // Recupera la partecipazione
        Partecipazione partecipazione = partecipazioneRepo.findByTeamAndHackathon(request.getTeamId(),
                request.getHackathonId());

        if (partecipazione == null) {
            throw new Exception("Nessuna partecipazione trovata per il team e hackathon specificati.");
        }

        User richiedente = userRepo.findById(request.getRichiedenteId());
        if (richiedente == null) {
            throw new Exception("Utente richiedente non trovato.");
        }

        // Valida appartenenza al team
        boolean isLeader = partecipazione.getTeam().getLeaderSquadra().getId().equals(request.getRichiedenteId());
        boolean isMembro = partecipazione.getTeam().getMembri().stream()
                .anyMatch(m -> m.getId().equals(request.getRichiedenteId()));

        if (!isLeader && !isMembro) {
            throw new Exception("L'utente richiedente non appartiene al team partecipante.");
        }

        // Crea e salva
        RichiestaSupporto nuovaRichiesta = new RichiestaSupporto(partecipazione, richiedente, request.getDescrizione());
        richiestaSupportoRepo.save(nuovaRichiesta);

        return nuovaRichiesta;
    }

}
