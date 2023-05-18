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

    public enum Role {
        USER, ADMIN
    }

    @Column(name = "reset_password_token")
    private String resetPasswordToken;

    public void setResetPasswordToken(String resetPasswordToken) {
        this.resetPasswordToken = resetPasswordToken;
    }

    private String emailVerificationToken;
    private boolean isEmailVerified = false;

    @ManyToMany
    @JoinTable(
            name = "user_packages",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "package_id")
    )
    private List<InvestmentPackage> investmentPackages = new ArrayList<>();

    @ElementCollection
    private List<BigDecimal> investedAmounts = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private User parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<User> referrals = new ArrayList<>();

    @Column
    private BigDecimal totalReferralCommission = BigDecimal.ZERO;

    @Column
    private BigDecimal totalNetworkCommission = BigDecimal.ZERO;

    @Column
    private int level;
    // In the User model
    private Long parentId;
    private Long leftChildId;
    private Long rightChildId;

}
