package com.mlmfreya.ferya2.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
public class InvestmentPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String description;
    //TODO: set default as active
    private Status status;
    private int duration;
    private BigDecimal returnOnInvestment;
    private BigDecimal minInvestmentAmount;  // New field for minimum investment amount
    private boolean cancellation = true;


    public enum Status{
        ACTIVE,DISABLE
    }
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}