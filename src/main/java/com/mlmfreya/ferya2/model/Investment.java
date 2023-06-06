package com.mlmfreya.ferya2.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "investments")
public class Investment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal investedAmount;

    private LocalDateTime investmentDate;

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference

    private User user;

    @ManyToOne
    @JoinColumn(name = "package_id")
    private InvestmentPackage investmentPackage;
    private LocalDateTime nextInterestPaymentDate;

    @ManyToOne
    @JoinColumn(name = "network_root_user_id", nullable = true)
    private User networkRootUser;




    // ... getters and setters ...
}
