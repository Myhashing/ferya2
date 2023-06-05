package com.mlmfreya.ferya2.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Entity
public class PriceData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String symbol;
    private String price;
    private LocalDateTime timestamp;

    // getters and setters
}
