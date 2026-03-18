package hackhub.app.Application.IUnitOfWork;

import hackhub.app.Application.IRepositories.IPartecipazioneRepository;
import hackhub.app.Application.IRepositories.ISottomissioneRepository;
import hackhub.app.Application.IRepositories.IValutazioneRepository;

/**
 * Interfaccia Unit of Work specializzata per operazioni su sottomissioni.
 * Espone solo i repository relativi a sottomissioni e valutazioni.
 */
public interface ISubmissionUnitOfWork {
    /**
     * Repository per le operazioni sulle sottomissioni.
     *
     * @return il repository delle sottomissioni
     */
    ISottomissioneRepository sottomissioneRepository();

    /**
     * Repository per le operazioni sulle valutazioni.
     *
     * @return il repository delle valutazioni
     */
    IValutazioneRepository valutazioneRepository();

    /**
     * Repository per le operazioni sulle partecipazioni.
     *
     * @return il repository delle partecipazioni
     */
    IPartecipazioneRepository partecipazioneRepository();
}
