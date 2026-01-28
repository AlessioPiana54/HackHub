package hackhub.app.Infrastructure.ExternalServices;

import hackhub.app.Application.IServices.IPaymentService;
import org.springframework.stereotype.Service;

@Service
public class MockPaymentService implements IPaymentService {

    @Override
    public void processPayment(String userId, double amount) {
        System.out.println("Processing mock payment of " + amount + " to user " + userId);
    }
}
