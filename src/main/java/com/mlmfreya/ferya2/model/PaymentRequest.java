package com.mlmfreya.ferya2.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private String userEmail;
    private String mobileNumber;
    @NotEmpty
    @Size(min = 6, max = 15)
    private String password;
    private String name;
    @Column
    private LocalDateTime created; // When the payment request was created


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "investment_package_id", referencedColumnName = "id")
    private InvestmentPackage investmentPackage;

    // setters and getters here
}