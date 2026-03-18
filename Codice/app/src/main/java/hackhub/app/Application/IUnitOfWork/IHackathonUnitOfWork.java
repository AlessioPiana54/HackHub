package hackhub.app.Application.IUnitOfWork;

import hackhub.app.Application.IRepositories.IHackathonRepository;
import hackhub.app.Application.IRepositories.IPartecipazioneRepository;

/**
 * Interfaccia Unit of Work specializzata per operazioni su hackathon.
 * Espone solo i repository relativi agli hackathon e partecipazioni.
 */
public interface IHackathonUnitOfWork {
    /**
     * Repository per le operazioni sugli hackathon.
     *
     * @return il repository degli hackathon
     */
    IHackathonRepository hackathonRepository();

    /**
     * Repository per le operazioni sulle partecipazioni.
     *
     * @return il repository delle partecipazioni
     */
    IPartecipazioneRepository partecipazioneRepository();
}
