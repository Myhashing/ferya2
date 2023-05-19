package com.mlmfreya.ferya2.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    private Long parentId;

    private String name;
    @NotEmpty
    @Email
    private String email;
    private String mobileNumber;
    @NotEmpty
    @Size(min = 6, max = 15)
    private String password;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "investment_package_id", referencedColumnName = "id")
    private InvestmentPackage investmentPackage;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
    // setters and getters here
}