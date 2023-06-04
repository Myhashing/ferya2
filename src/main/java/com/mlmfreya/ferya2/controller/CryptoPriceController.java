package com.mlmfreya.ferya2.controller;

import com.mlmfreya.ferya2.model.PriceData;
import com.mlmfreya.ferya2.repository.PriceDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CryptoPriceController {

    @Autowired
    private PriceDataRepository priceDataRepository;

    @GetMapping("/price/{symbol}")
    public PriceData getPrice(@PathVariable String symbol) {
        return priceDataRepository.findTopBySymbolOrderByTimestampDesc(symbol);
    }
}