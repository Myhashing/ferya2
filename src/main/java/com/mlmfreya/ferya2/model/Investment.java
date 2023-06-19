package com.mlmfreya.ferya2.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "investments")
public class Investment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal investedAmount;

    private LocalDateTime investmentDate;
    private BigDecimal pendingInterest;
    @OneToMany(mappedBy = "investment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InvestmentHistory> history = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "package_id")
    private InvestmentPackage investmentPackage;
    private LocalDateTime nextInterestPaymentDate;

    @ManyToOne
    @JoinColumn(name = "network_root_user_id", nullable = true)
    private User networkRootUser;



    public void upgradeInvestment(BigDecimal additionalAmount) {
        this.investedAmount = this.investedAmount.add(additionalAmount);

        InvestmentHistory historyEntry = new InvestmentHistory();
        historyEntry.setInvestment(this);
        historyEntry.setAdditionalAmount(additionalAmount);
        historyEntry.setDate(LocalDateTime.now());

        this.history.add(historyEntry);
    }

}
