package com.mlmfreya.ferya2.controller;

import com.mlmfreya.ferya2.service.TransactionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentStatusController {
    private final TransactionService transactionService;

    public PaymentStatusController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/payment-status/{walletAddress}")
    public String getPaymentStatus(@PathVariable String walletAddress) {
        return transactionService.getTransactionStatus(walletAddress).toString();
    }
}
