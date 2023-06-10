package com.mlmfreya.ferya2.controller;

import com.mlmfreya.ferya2.service.TronWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
public class TronController {

    @Autowired
    private TronWebService tronWebService;
    @GetMapping("/public/api/wallet/balance/{walletAddress}")
    public BigDecimal checkWalletBalance(@PathVariable String walletAddress) {
        return tronWebService.checkWalletBalance(walletAddress);
    }

}
