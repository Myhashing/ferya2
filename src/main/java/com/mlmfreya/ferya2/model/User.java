package com.mlmfreya.ferya2.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@Data
@Table(name = "app_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String fullName;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String tronWalletAddress;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(unique = true)
    private String referralCode;

    public enum Role {
        USER, ADMIN
    }

    private String resetPasswordToken;
    private String emailVerificationToken;
    private boolean isEmailVerified = false;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Investment> investments = new ArrayList<>();



    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    private User parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<User> referrals = new ArrayList<>();

    private BigDecimal totalReferralCommission = BigDecimal.ZERO;
    private BigDecimal totalNetworkCommission = BigDecimal.ZERO;

    private int level;

    @OneToOne(fetch = FetchType.EAGER)
    private User leftChild;

    @OneToOne(fetch = FetchType.EAGER)
    private User rightChild;

    public boolean hasBothChildren() {
        return leftChild != null && rightChild != null;
    }

    @OneToMany(mappedBy = "beneficiary", cascade = CascadeType.ALL)
    private List<Commission> commissions = new ArrayList<>();

    public boolean hasInvestment() {
        return !this.investments.isEmpty();
    }


    private BigDecimal balance;

    public BigDecimal getTotalCommission() {
        return commissions.stream()
                .map(Commission::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getInvestedAmount() {
        if(this.investments.isEmpty()) {
            throw new RuntimeException("No investments found for this user.");
        } else {
            return this.investments.get(0).getInvestedAmount();
        }
    }

    public BigDecimal getTotalInvestedAmount() {
        if(this.investments.isEmpty()) {
            throw new RuntimeException("No investments found for this user.");
        } else {
            return this.investments.stream()
                    .map(Investment::getInvestedAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }


}
