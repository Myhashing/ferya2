package com.mlmfreya.ferya2.repository;

import com.mlmfreya.ferya2.model.InvestmentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvestmentHistoryRepository extends JpaRepository<InvestmentHistory, Long> {
}