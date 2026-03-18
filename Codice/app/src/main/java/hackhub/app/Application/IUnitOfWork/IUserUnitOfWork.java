package hackhub.app.Application.IUnitOfWork;

import hackhub.app.Application.IRepositories.IUserRepository;

/**
 * Interfaccia Unit of Work specializzata per operazioni su utenti.
 * Espone solo i repository relativi agli utenti.
 */
public interface IUserUnitOfWork {
    /**
     * Repository per le operazioni sugli utenti.
     *
     * @return il repository degli utenti
     */
    IUserRepository userRepository();
}
