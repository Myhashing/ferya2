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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<Investment> investments = new ArrayList<>();



    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    private User parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
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

    @OneToMany(mappedBy = "beneficiary", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
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

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                ", tronWalletAddress='" + tronWalletAddress + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", referralCode='" + referralCode + '\'' +
                ", resetPasswordToken='" + resetPasswordToken + '\'' +
                ", emailVerificationToken='" + emailVerificationToken + '\'' +
                ", isEmailVerified=" + isEmailVerified +
                ", totalReferralCommission=" + totalReferralCommission +
                ", totalNetworkCommission=" + totalNetworkCommission +
                ", level=" + level +
                ", balance=" + balance +
                '}';
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((fullName == null) ? 0 : fullName.hashCode());
        result = prime * result + ((referralCode == null) ? 0 : referralCode.hashCode());
        // Don't include parent or children in the hash code calculation
        return result;
    }






}
