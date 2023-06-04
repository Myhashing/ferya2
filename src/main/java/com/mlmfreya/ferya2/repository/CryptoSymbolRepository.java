package com.mlmfreya.ferya2.repository;

import com.mlmfreya.ferya2.model.CryptoSymbol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CryptoSymbolRepository extends JpaRepository<CryptoSymbol, Long> {
}

