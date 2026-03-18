package hackhub.app.Application.IUnitOfWork;

/**
 * Interfaccia che definisce l'Unit of Work per l'applicazione.
 * Fornisce un punto di accesso centralizzato a tutti i repository, garantendo
 * che condividano lo stesso contesto di persistenza durante una transazione.
 * Estende tutte le sotto-interfacce tematiche per rispettare l'Interface Segregation Principle.
 */
public interface IUnitOfWork
  extends
    IUserUnitOfWork,
    ITeamUnitOfWork,
    IHackathonUnitOfWork,
    ISubmissionUnitOfWork,
    ISupportUnitOfWork {
  // Tutti i metodi sono ereditati dalle sotto-interfacce
  // Questo mantiene la compatibilità con il codice esistente
  // mentre permette ai service di usare solo le interfacce necessarie
}
