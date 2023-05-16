package com.mlmfreya.ferya2.repository;

import com.mlmfreya.ferya2.model.PaymentRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRequestRepository extends JpaRepository<PaymentRequest, Long> {

    Optional<PaymentRequest> findPaymentRequestByWalletAddress(String walletAddress);
}