package com.mlmfreya.ferya2.service;
import com.mlmfreya.ferya2.model.PaymentRequest;
import com.mlmfreya.ferya2.model.User;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class PaymentWatcherService {
    // Inject necessary services
    private final UserService userService;
    private final WalletService walletService;
    private final TransactionService transactionService;
    private final EmailService emailService; // You'll need a service for sending emails

    public PaymentWatcherService(UserService userService,
                                 WalletService walletService,
                                 TransactionService transactionService,
                                 EmailService emailService) {
        this.userService = userService;
        this.walletService = walletService;
        this.transactionService = transactionService;
        this.emailService = emailService;
    }

    public void watchPayment(String walletAddress, BigDecimal amount, String userEmail) {
        // Start a new thread to watch for incoming payments
        new Thread(() -> {
            boolean paymentReceived = false;
            while (!paymentReceived) {
                BigDecimal balance = walletService.getBalance(walletAddress);
                if (balance.compareTo(amount) >= 0) {
                    processPayment(userEmail, amount);
                    paymentReceived = true;
                }
                try {
                    Thread.sleep(5000); // sleep for 5 seconds before checking again
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void processPayment(String userEmail, BigDecimal amount) {
        PaymentRequest paymentRequest = P
        // Register the user
        User user = userService.registerUser(userEmail);
        // Add the purchased package to the user's account
        userService.addPackageToUser(user, amount);
        // Send a welcome email to the user
        emailService.sendWelcomeEmail(user);
        // Update the transaction status
        transactionService.updateTransactionStatus(userEmail, "Payment received");
    }
}
