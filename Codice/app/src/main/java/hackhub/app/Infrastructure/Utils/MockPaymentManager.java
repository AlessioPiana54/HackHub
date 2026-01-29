package hackhub.app.Infrastructure.Utils;

import org.springframework.stereotype.Service;

import hackhub.app.Application.Utils.IPaymentManager;

@Service
public class MockPaymentManager implements IPaymentManager {

    @Override
    public void processPayment(String userId, double amount) {
        System.out.println("Processing mock payment of " + amount + " to user " + userId);
    }
}
