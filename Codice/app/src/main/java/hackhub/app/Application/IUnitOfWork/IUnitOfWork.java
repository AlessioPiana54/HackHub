package hackhub.app.Application.IUnitOfWork;

import hackhub.app.Application.IRepositories.*;

/**
 * Interfaccia che definisce l'Unit of Work per l'applicazione.
 * Fornisce un punto di accesso centralizzato a tutti i repository, garantendo
 * che condividano lo stesso contesto di persistenza durante una transazione.
 */
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
