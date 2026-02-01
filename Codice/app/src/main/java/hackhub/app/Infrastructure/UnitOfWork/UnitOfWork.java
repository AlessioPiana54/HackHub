package hackhub.app.Infrastructure.UnitOfWork;

import org.springframework.stereotype.Component;

import hackhub.app.Application.IRepositories.*;
import hackhub.app.Application.IUnitOfWork.IUnitOfWork;

@Component
public class UnitOfWork implements IUnitOfWork {

    private final IHackathonRepository hackathonRepository;
    private final IInvitoRepository invitoRepository;
    private final IPartecipazioneRepository partecipazioneRepository;
    private final IRichiestaSupportoRepository richiestaSupportoRepository;
    private final ISegnalazioneRepository segnalazioneRepository;
    private final ISottomissioneRepository sottomissioneRepository;
    private final ITeamRepository teamRepository;
    private final IUserRepository userRepository;
    private final IValutazioneRepository valutazioneRepository;

    public UnitOfWork(
            IHackathonRepository hackathonRepository,
            IInvitoRepository invitoRepository,
            IPartecipazioneRepository partecipazioneRepository,
            IRichiestaSupportoRepository richiestaSupportoRepository,
            ISegnalazioneRepository segnalazioneRepository,
            ISottomissioneRepository sottomissioneRepository,
            ITeamRepository teamRepository,
            IUserRepository userRepository,
            IValutazioneRepository valutazioneRepository) {
        this.hackathonRepository = hackathonRepository;
        this.invitoRepository = invitoRepository;
        this.partecipazioneRepository = partecipazioneRepository;
        this.richiestaSupportoRepository = richiestaSupportoRepository;
        this.segnalazioneRepository = segnalazioneRepository;
        this.sottomissioneRepository = sottomissioneRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.valutazioneRepository = valutazioneRepository;
    }

    @Override
    public IHackathonRepository hackathonRepository() {
        return hackathonRepository;
    }

    @Override
    public IInvitoRepository invitoRepository() {
        return invitoRepository;
    }

    @Override
    public IPartecipazioneRepository partecipazioneRepository() {
        return partecipazioneRepository;
    }

    @Override
    public IRichiestaSupportoRepository richiestaSupportoRepository() {
        return richiestaSupportoRepository;
    }

    @Override
    public ISegnalazioneRepository segnalazioneRepository() {
        return segnalazioneRepository;
    }

    @Override
    public ISottomissioneRepository sottomissioneRepository() {
        return sottomissioneRepository;
    }

    @Override
    public ITeamRepository teamRepository() {
        return teamRepository;
    }

    @Override
    public IUserRepository userRepository() {
        return userRepository;
    }

    @Override
    public IValutazioneRepository valutazioneRepository() {
        return valutazioneRepository;
    }
}
