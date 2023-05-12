package com.mlmfreya.ferya2.service;


import com.mlmfreya.ferya2.model.Order;
import com.mlmfreya.ferya2.model.PaymentRequest;
import com.mlmfreya.ferya2.model.Transaction;
import com.mlmfreya.ferya2.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentGatewayService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private OrderService orderService;


}
