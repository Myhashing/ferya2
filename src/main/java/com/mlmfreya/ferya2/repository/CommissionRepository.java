package com.mlmfreya.ferya2.repository;

import com.mlmfreya.ferya2.model.Commission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommissionRepository extends JpaRepository<Commission, Long> {
}