package com.mlmfreya.ferya2.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fromAddress; // The wallet address of the payer

    @Column(nullable = false)
    private String toAddress; // The wallet address of the payee

    @Column(nullable = false)
    private BigDecimal amount; // The amount of USDT transacted

    @Column(nullable = false)
    private String transactionHash; // The hash of the transaction on the blockchain

    @Column
    private LocalDateTime timestamp; // When the transaction occurred

    // setters and getters here
}