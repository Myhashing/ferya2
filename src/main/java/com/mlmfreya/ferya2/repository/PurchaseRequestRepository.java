package com.mlmfreya.ferya2.repository;

import com.mlmfreya.ferya2.model.PurchaseRequestEarly;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRequestRepository extends JpaRepository<PurchaseRequestEarly, Long> {
}