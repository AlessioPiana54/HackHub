package hackhub.app.Application.IUnitOfWork;

import hackhub.app.Application.IRepositories.IRichiestaSupportoRepository;
import hackhub.app.Application.IRepositories.ISegnalazioneRepository;

/**
 * Interfaccia Unit of Work specializzata per operazioni di supporto.
 * Espone solo i repository relativi a richieste di supporto e segnalazioni.
 */
public interface ISupportUnitOfWork {
    /**
     * Repository per le operazioni sulle richieste di supporto.
     *
     * @return il repository delle richieste di supporto
     */
    IRichiestaSupportoRepository richiestaSupportoRepository();

    /**
     * Repository per le operazioni sulle segnalazioni.
     *
     * @return il repository delle segnalazioni
     */
    ISegnalazioneRepository segnalazioneRepository();
}
