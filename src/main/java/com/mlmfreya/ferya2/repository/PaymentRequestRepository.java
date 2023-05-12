package com.mlmfreya.ferya2.repository;

import com.mlmfreya.ferya2.model.PaymentRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRequestRepository extends JpaRepository<PaymentRequest, Long> {
}