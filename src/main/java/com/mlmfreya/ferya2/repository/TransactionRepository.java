package com.mlmfreya.ferya2.repository;

import com.mlmfreya.ferya2.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Transaction findTransactionByToAddress(String address);

}