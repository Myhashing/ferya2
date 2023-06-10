package com.mlmfreya.ferya2.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "investment_history")
public class InvestmentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "investment_id")
    private Investment investment;

    private BigDecimal additionalAmount;

    private LocalDateTime date;

    // ... getters and setters ...
}