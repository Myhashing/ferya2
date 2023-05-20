package com.mlmfreya.ferya2.repository;

import com.mlmfreya.ferya2.model.Payout;
import com.mlmfreya.ferya2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayoutRepository extends JpaRepository<Payout, Long> {
    List<Payout> findByUser(User user);
}
