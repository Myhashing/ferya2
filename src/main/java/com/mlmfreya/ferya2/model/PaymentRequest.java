package com.mlmfreya.ferya2.model;


import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class PaymentRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal amount; // The amount of USDT to be paid

    @Column(nullable = false)
    private String walletAddress; // The wallet address for the payment

    @Column
    private String fromAddress;
    @Column
    private LocalDateTime created; // When the payment request was created

    // setters and getters here
}