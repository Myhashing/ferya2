package com.mlmfreya.ferya2.service;
import com.mlmfreya.ferya2.model.Wallet;
import com.mlmfreya.ferya2.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WalletService {

    private final WalletRepository walletRepository;


    @Autowired
    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Wallet saveWallet(Wallet wallet) {
        return walletRepository.save(wallet);
    }




}
