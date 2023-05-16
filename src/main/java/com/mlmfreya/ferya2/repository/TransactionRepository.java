package com.mlmfreya.ferya2.repository;

import com.mlmfreya.ferya2.model.Transaction;
import com.mlmfreya.ferya2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Transaction findByUser(User user);
}