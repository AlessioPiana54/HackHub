package hackhub.app.Application.IUnitOfWork;

import hackhub.app.Application.IRepositories.*;

public interface IUnitOfWork {
    IHackathonRepository hackathonRepository();

    IInvitoRepository invitoRepository();

    IPartecipazioneRepository partecipazioneRepository();

    IRichiestaSupportoRepository richiestaSupportoRepository();

    ISegnalazioneRepository segnalazioneRepository();

    ISottomissioneRepository sottomissioneRepository();

    ITeamRepository teamRepository();

    IUserRepository userRepository();

    IValutazioneRepository valutazioneRepository();
}
