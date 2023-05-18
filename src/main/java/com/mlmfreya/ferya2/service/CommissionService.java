package com.mlmfreya.ferya2.service;

import com.mlmfreya.ferya2.model.User;
import com.mlmfreya.ferya2.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
@Service
public class CommissionService {

    private static final BigDecimal REFERRAL_COMMISSION_RATE = BigDecimal.valueOf(0.10);
    private static final BigDecimal NETWORK_COMMISSION_RATE = BigDecimal.valueOf(0.01);
    private static final BigDecimal MAX_COMMISSION_MULTIPLIER = BigDecimal.valueOf(30);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommissionRepository commissionRepository;

    public void calculateAndDistributeCommissions(User newUser, BigDecimal investmentAmount) {
        // Calculate referral commission for parent
        User parent = newUser.getParent();
        if (parent != null && parent.getLeftChild() != null && parent.getRightChild() != null) {
            BigDecimal referralCommission = investmentAmount.multiply(REFERRAL_COMMISSION_RATE);
            distributeCommission(parent, newUser, referralCommission, Commission.Type.REFERRAL);
        }

        // Calculate and distribute network commissions up to 15 levels
        distributeNetworkCommissions(newUser, investmentAmount, 15);
    }

    private void distributeNetworkCommissions(User user, BigDecimal investmentAmount, int level) {
        if (level == 0 || user.getParent() == null) {
            return;
        }

        BigDecimal networkCommission = investmentAmount.multiply(NETWORK_COMMISSION_RATE);
        distributeCommission(user.getParent(), user, networkCommission, Commission.Type.NETWORK);

        distributeNetworkCommissions(user.getParent(), investmentAmount, level - 1);
    }

    private void distributeCommission(User parent, User investor, BigDecimal commissionAmount, Commission.Type type) {
        // Check if both children of the parent have invested
        if (parent.getLeftChild() != null && parent.getRightChild() != null) {
            // Create a new commission
            Commission commission = new Commission();
            commission.setInvestor(investor);
            commission.setBeneficiary(parent);
            commission.setAmount(commissionAmount);
            commission.setType(type);

            // Save the commission
            commissionRepository.save(commission);
        }
    }
}
