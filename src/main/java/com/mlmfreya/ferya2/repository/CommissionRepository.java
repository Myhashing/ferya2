package com.mlmfreya.ferya2.repository;

import com.mlmfreya.ferya2.model.Commission;
import com.mlmfreya.ferya2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface CommissionRepository extends JpaRepository<Commission, Long> {
    List<Commission> findByBeneficiary(User beneficiary);

    @Query("SELECT COUNT(c) FROM Commission c WHERE c.type = :type")
    Long countCommissionsByType(@Param("type") Commission.Type type);

    @Query("SELECT SUM(c.amount) FROM Commission c WHERE c.type = :type")
    BigDecimal sumCommissionsByType(@Param("type") Commission.Type type);



}