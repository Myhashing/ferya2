package com.mlmfreya.ferya2.repository;

import com.mlmfreya.ferya2.model.PaymentRequest;
import com.mlmfreya.ferya2.model.PaymentRequestUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaymentRequestRepository extends JpaRepository<PaymentRequest, Long> {
    PaymentRequest findPaymentRequestByWalletAddress(String walletAdress);


}