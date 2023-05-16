package com.mlmfreya.ferya2.repository;

import com.mlmfreya.ferya2.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
}