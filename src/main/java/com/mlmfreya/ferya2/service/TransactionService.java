package com.mlmfreya.ferya2.service;

import com.mlmfreya.ferya2.model.InvestmentPackage;
import com.mlmfreya.ferya2.model.PaymentRequest;
import com.mlmfreya.ferya2.model.Transaction;
import com.mlmfreya.ferya2.repository.PaymentRequestRepository;
import com.mlmfreya.ferya2.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final PaymentRequestRepository paymentRequestRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, PaymentRequestRepository paymentRequestRepository) {
        this.transactionRepository = transactionRepository;
        this.paymentRequestRepository= paymentRequestRepository;
    }

    public void createTransaction(PaymentRequest paymentRequest, InvestmentPackage investmentPackage) {
        Transaction transaction = new Transaction();
        // Assuming you have a "fromAddress" in PaymentRequest
        transaction.setFromAddress(paymentRequest.getFromAddress());
        transaction.setToAddress(paymentRequest.getWalletAddress());
        transaction.setAmount(paymentRequest.getAmount());
        // You'll need to get the transaction hash from the API or set it later
        transaction.setTransactionHash("");
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setInvestmentPackage(investmentPackage);
        transaction.setStatus(Transaction.Status.pending);
        transactionRepository.save(transaction);
        createPaymentRequest(paymentRequest);
    }

    public void createPaymentRequest(PaymentRequest paymentRequest){
        paymentRequestRepository.save(paymentRequest);
    }

    public void updateTransactionStatus(String walletAddress, Transaction.Status status) {
        Transaction transaction = transactionRepository.findTransactionByToAddress(walletAddress);
        if (transaction != null) {
            transaction.setStatus(status);
            transactionRepository.save(transaction);
        } else {
            // Handle case where no transaction corresponds to the email
        }
    }


    public Transaction.Status getTransactionStatus(String walletAddress) {

        Transaction transaction = transactionRepository.findTransactionByToAddress(walletAddress);
        return transaction.getStatus();

    }
}
