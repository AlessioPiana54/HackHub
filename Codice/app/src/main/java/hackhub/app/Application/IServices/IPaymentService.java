package hackhub.app.Application.IServices;

public interface IPaymentService {
    void processPayment(String userId, double amount);
}
