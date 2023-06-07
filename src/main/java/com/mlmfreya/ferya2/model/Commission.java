package com.mlmfreya.ferya2.model;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "commissions")
public class Commission {

    public enum Type {
        REFERRAL,
        NETWORK
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "beneficiary_id", nullable = false)
    private User beneficiary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        PENDING,
        PAID
    }
    // getters and setters
}
