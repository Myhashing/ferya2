package com.mlmfreya.ferya2.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class PriceData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String symbol;
    private String price;
    private LocalDateTime timestamp;

    public PriceData(String symbol, String price) {
        this.symbol = symbol;
        this.price = price;
        this.timestamp = LocalDateTime.now();
    }

    // getters and setters
}
