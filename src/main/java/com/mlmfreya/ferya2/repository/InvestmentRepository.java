package com.mlmfreya.ferya2.repository;

import com.mlmfreya.ferya2.model.Investment;
import com.mlmfreya.ferya2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InvestmentRepository extends JpaRepository<Investment, Long> {
    @Query("SELECT i FROM Investment i JOIN FETCH i.history WHERE i.id = :id")
    Optional<Investment> findByIdWithHistory(@Param("id") Long id);
}