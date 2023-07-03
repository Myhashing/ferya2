package com.mlmfreya.ferya2.service;

import com.mlmfreya.ferya2.model.Commission;
import com.mlmfreya.ferya2.model.User;
import com.mlmfreya.ferya2.repository.CommissionRepository;
import com.mlmfreya.ferya2.repository.UserRepository;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.mlmfreya.ferya2.model.Commission;
import com.mlmfreya.ferya2.model.Commission.Type;
import com.mlmfreya.ferya2.repository.CommissionRepository;
@Service
public class CommissionService {

    private static final BigDecimal REFERRAL_COMMISSION_RATE = BigDecimal.valueOf(0.10);
    private static final BigDecimal NETWORK_COMMISSION_RATE = BigDecimal.valueOf(0.01);
    private static final BigDecimal MAX_COMMISSION_MULTIPLIER = BigDecimal.valueOf(30);

    private List<Commission> pendingCommissions = new ArrayList<>();
    private final CommissionRepository commissionRepository;
    private final UserRepository userRepository;



    public List<Commission> getPendingCommissions() {
        return commissionRepository.findAll().stream()
                .filter(commission -> commission.getStatus() == Commission.Status.PENDING)
                .collect(Collectors.toList());
    }

    @Autowired
    public CommissionService(CommissionRepository commissionRepository, UserRepository userRepository) {
        this.commissionRepository = commissionRepository;
        this.userRepository = userRepository;
    }
    public void payoutCommission(Long commissionId) {
        Commission commission = commissionRepository.findById(commissionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid commission ID: " + commissionId));

        User beneficiary = commission.getBeneficiary();
        BigDecimal commissionAmount = commission.getAmount();
        beneficiary.setBalance(beneficiary.getBalance().add(commissionAmount));
        commission.setStatus(Commission.Status.PAID);

        userRepository.save(beneficiary);
        commissionRepository.save(commission);
    }





    @Transactional
    public void calculateAndDistributeCommissions(User parent) {
        // Calculate referral commission for parent
        if (parent != null && parent.hasBothChildren()) {
            // Get the investment amounts of both children
            BigDecimal leftChildInvestment = parent.getLeftChild().getInvestments().getInvestedAmount();
            BigDecimal rightChildInvestment = parent.getRightChild().getInvestments().getInvestedAmount();

            // Determine the lower investment amount
            BigDecimal lowerInvestmentAmount = leftChildInvestment.compareTo(rightChildInvestment) < 0 ? leftChildInvestment : rightChildInvestment;

            // Calculate the referral commission based on the lower investment amount
            BigDecimal referralCommission = lowerInvestmentAmount.multiply(REFERRAL_COMMISSION_RATE);

            // Add the commission to the parent's totalReferralCommission
            parent.setTotalReferralCommission(parent.getTotalReferralCommission().add(referralCommission));
            userRepository.save(parent);

            // Store the commission in the commission table
            addCommission(parent, referralCommission, Commission.Type.REFERRAL);
        }


    }

   @Transactional
    public void distributeNetworkCommissions(User parent,  BigDecimal investmentAmount, int level) {
        if (level == 0 || parent.getParent() == null) {
            return;
        }

        if (parent.hasBothChildren()) {
            BigDecimal networkCommission = investmentAmount.multiply(NETWORK_COMMISSION_RATE);
            addCommission(parent, networkCommission, Type.NETWORK);
        }

        distributeNetworkCommissions(parent.getParent(), investmentAmount, level - 1);
    }

    @Transactional
    public void addCommission(User beneficiary, BigDecimal amount, Type type) {
            Commission commission = new Commission();
            commission.setBeneficiary(beneficiary);
            commission.setType(type);
            commission.setAmount(amount);
            commission.setStatus(Commission.Status.PAID);
            commissionRepository.save(commission);
    }

    @Transactional
    public void processPendingCommissions() {
        for (Commission commission : pendingCommissions) {
            commissionRepository.save(commission);
            userRepository.save(commission.getBeneficiary());
        }
        pendingCommissions.clear();
    }
    @Transactional
    public void calculateMonthlyCommissions() {
        List<User> allUsers = userRepository.findAll();

        for (User user : allUsers) {
            if (user.hasInvestment()) {
                BigDecimal investmentAmount = user.getTotalInvestedAmount(); // Assumes one investment per user
                calculateAndDistributeCommissions(user);
            }
        }
    }

    public Long countCommissionsByType(Commission.Type type) {
        return commissionRepository.countCommissionsByType(type);
    }

    public BigDecimal sumCommissionsByType(Commission.Type type) {
        return commissionRepository.sumCommissionsByType(type);
    }


}
