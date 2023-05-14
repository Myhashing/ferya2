package com.mlmfreya.ferya2.service;

import com.mlmfreya.ferya2.model.PaymentRequest;
import com.mlmfreya.ferya2.model.Transaction;
import com.mlmfreya.ferya2.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void createTransaction(PaymentRequest paymentRequest) {
        Transaction transaction = new Transaction();
        // Assuming you have a "fromAddress" in PaymentRequest
        transaction.setFromAddress(paymentRequest.getFromAddress());
        transaction.setToAddress(paymentRequest.getWalletAddress());
        transaction.setAmount(paymentRequest.getAmount());
        // You'll need to get the transaction hash from the API or set it later
        transaction.setTransactionHash("");
        transaction.setTimestamp(LocalDateTime.now());

        transactionRepository.save(transaction);
    }

}
