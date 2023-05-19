package com.mlmfreya.ferya2.repository;

import com.mlmfreya.ferya2.model.Investment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvestmentRepository extends JpaRepository<Investment, Long> {
}