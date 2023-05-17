package com.mlmfreya.ferya2.service;
import com.mlmfreya.ferya2.model.PaymentRequest;
import com.mlmfreya.ferya2.model.Transaction;
import com.mlmfreya.ferya2.model.User;
import com.mlmfreya.ferya2.repository.PaymentRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class PaymentWatcherService {
    // Inject necessary services
    private final UserService userService;
    private final WalletService walletService;
    private final TransactionService transactionService;
    private final EmailService emailService; // You'll need a service for sending emails

    @Autowired
    private TronWebService tronWebService;

    private final PaymentRequestRepository paymentRequestRepository;

    public PaymentWatcherService(UserService userService,
                                 WalletService walletService,
                                 TransactionService transactionService,
                                 EmailService emailService,
                                 PaymentRequestRepository paymentRequestRepository) {
        this.userService = userService;
        this.walletService = walletService;
        this.transactionService = transactionService;
        this.emailService = emailService;
        this.paymentRequestRepository = paymentRequestRepository;
    }

    public void watchPayment(String walletAddress, BigDecimal amount, String userEmail) {
        // Start a new thread to watch for incoming payments
        new Thread(() -> {
            boolean paymentReceived = false;
            while (!paymentReceived) {
                BigDecimal balance = tronWebService.checkWalletBalance(walletAddress);
                if (balance.compareTo(amount) >= 0) {
                    processPayment(walletAddress, amount);
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

    private void processPayment(String walletAdress, BigDecimal amount) {
        PaymentRequest paymentRequest = paymentRequestRepository.findPaymentRequestByWalletAddress(walletAdress);
        User user = new User();
        user.setPassword(paymentRequest.getPassword());
        user.setEmail(paymentRequest.getEmail());
        user.setFullName(paymentRequest.getName());
        user.setTronWalletAddress(paymentRequest.getWalletAddress());
        // Register the user
        User userNew = userService.registerUser(user);
        // Add the purchased package to the user's account
        userService.addPackageToUser(userNew, paymentRequest.getInvestmentPackage() ,amount);
        // Send a welcome email to the user
        emailService.sendWelcomeEmail(user);
        // Update the transaction status
        transactionService.updateTransactionStatus(paymentRequest.getWalletAddress(), Transaction.Status.complete);
    }
}
