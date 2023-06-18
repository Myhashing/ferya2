package com.mlmfreya.ferya2.repository;

import com.mlmfreya.ferya2.model.PaymentRequestUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaymentRequestUserRepository extends JpaRepository<PaymentRequestUser,Long> {
    @Query("SELECT pru FROM PaymentRequestUser pru JOIN FETCH pru.user u JOIN FETCH u.investments WHERE pru.id = :id")
    Optional<PaymentRequestUser> findByIdWithUserAndInvestments(@Param("id") Long id);
}
