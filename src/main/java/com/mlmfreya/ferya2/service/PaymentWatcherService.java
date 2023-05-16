package com.mlmfreya.ferya2.service;
import com.mlmfreya.ferya2.exception.ResourceNotFoundException;
import com.mlmfreya.ferya2.model.PaymentRequest;
import com.mlmfreya.ferya2.model.Transaction;
import com.mlmfreya.ferya2.model.User;
import com.mlmfreya.ferya2.repository.PaymentRequestRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class PaymentWatcherService {
    // Inject necessary services
    private final UserService userService;
    private final WalletService walletService;
    private final TransactionService transactionService;
    private final EmailService emailService;
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
                BigDecimal balance = walletService.getBalance(walletAddress);
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

    private void processPayment(String walletAddress, BigDecimal amount) {
        PaymentRequest paymentRequest = paymentRequestRepository.
                findPaymentRequestByWalletAddress(walletAddress).orElseThrow(()-> new ResourceNotFoundException("paymentRequest","walletAddress: ", walletAddress));
        // Register the user
        User userNew = new User();
        userNew.setPassword(paymentRequest.getPassword());
        userNew.setEmail(paymentRequest.getUserEmail());
        userNew.setFullName(paymentRequest.getName());
        userNew.setTronWalletAddress(paymentRequest.getWalletAddress());
        User user = userService.registerUser(userNew);
        // Add the purchased package to the user's account
        userService.addPackageToUser(user,paymentRequest.getInvestmentPackage(), amount);
        // Send a welcome email to the user
        emailService.sendWelcomeEmail(user);
        // Update the transaction status
        transactionService.updateTransactionStatus(user, Transaction.Status.Payment_Approved);
    }
}
