package com.mlmfreya.ferya2.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class Payout {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PayoutType type;

    private LocalDateTime payoutDate;

    // constructors, getters, setters...

    public enum PayoutType {
        COMMISSION,
        INTEREST,
        WITHDRAWAL
    }



    // getters and setters

}
