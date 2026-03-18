package hackhub.app.Application.IUnitOfWork;

import hackhub.app.Application.IRepositories.IInvitoRepository;
import hackhub.app.Application.IRepositories.IPartecipazioneRepository;
import hackhub.app.Application.IRepositories.ITeamRepository;

/**
 * Interfaccia Unit of Work specializzata per operazioni su team.
 * Espone solo i repository relativi ai team e inviti.
 */
public interface ITeamUnitOfWork {
    /**
     * Repository per le operazioni sui team.
     *
     * @return il repository dei team
     */
    ITeamRepository teamRepository();

    /**
     * Repository per le operazioni sugli inviti.
     *
     * @return il repository degli inviti
     */
    IInvitoRepository invitoRepository();

    /**
     * Repository per le operazioni sulle partecipazioni.
     *
     * @return il repository delle partecipazioni
     */
    IPartecipazioneRepository partecipazioneRepository();
}
