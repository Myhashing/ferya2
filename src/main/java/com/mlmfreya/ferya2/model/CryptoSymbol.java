package com.mlmfreya.ferya2.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data

@Entity
public class CryptoSymbol {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String symbol;
    private String name;
    private String image;
    private double market_cap;
    private int market_cap_rank;
    private double fully_diluted_valuation;
    private double total_volume;
    private double market_cap_change_24h;
    private double market_cap_change_percentage_24h;
    private double circulating_supply;
    private double total_supply;
    private double max_supply;
    @Column(name = "ath_column")
    private double ath;
    @Column(name = "ath_change_percentage_column")
    private double ath_change_percentage;

    private LocalDateTime ath_date;
    private double atl;
    private double atl_change_percentage;
    private LocalDateTime atl_date;
    private LocalDateTime last_updated;

    // getters and setters
}
