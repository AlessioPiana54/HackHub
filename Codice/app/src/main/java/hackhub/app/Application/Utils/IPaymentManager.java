package hackhub.app.Application.Utils;

/**
 * Interfaccia per la gestione dei pagamenti.
 */
public interface IPaymentManager {
    /**
     * Processa un pagamento per un determinato utente.
     *
     * @param userId L'ID dell'utente che effettua il pagamento.
     * @param amount L'importo da addebitare.
     */
    void processPayment(String userId, double amount);
}
