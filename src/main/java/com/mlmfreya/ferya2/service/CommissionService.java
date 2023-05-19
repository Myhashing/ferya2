package com.mlmfreya.ferya2.service;

import com.mlmfreya.ferya2.model.Commission;
import com.mlmfreya.ferya2.model.User;
import com.mlmfreya.ferya2.repository.CommissionRepository;
import com.mlmfreya.ferya2.repository.UserRepository;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import com.mlmfreya.ferya2.model.Commission;
import com.mlmfreya.ferya2.model.Commission.Type;
import com.mlmfreya.ferya2.repository.CommissionRepository;
@Service
public class CommissionService {

    private static final BigDecimal REFERRAL_COMMISSION_RATE = BigDecimal.valueOf(0.10);
    private static final BigDecimal NETWORK_COMMISSION_RATE = BigDecimal.valueOf(0.01);
    private static final BigDecimal MAX_COMMISSION_MULTIPLIER = BigDecimal.valueOf(30);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommissionRepository commissionRepository;

    @Transactional
    public void calculateAndDistributeCommissions(User newUser, BigDecimal investmentAmount) {
        // Calculate referral commission for parent
        User parent = newUser.getParent();
        if (parent != null && parent.hasBothChildren()) {
            BigDecimal referralCommission = investmentAmount.multiply(REFERRAL_COMMISSION_RATE);
            addCommission(parent, newUser, referralCommission, Commission.Type.REFERRAL);
        }

        // Calculate and distribute network commissions up to 15 levels
        // Network commissions rule
        if (parent != null) {
            distributeNetworkCommissions(parent, newUser, investmentAmount, 15);
        }
    }

    @Transactional
    public void distributeNetworkCommissions(User parent, User newUser, BigDecimal investmentAmount, int level) {
        if (level == 0 || parent.getParent() == null) {
            return;
        }

        if (parent.hasBothChildren()) {
            BigDecimal networkCommission = investmentAmount.multiply(NETWORK_COMMISSION_RATE);
            addCommission(parent, newUser, networkCommission, Type.NETWORK);
        }

        distributeNetworkCommissions(parent.getParent(), newUser, investmentAmount, level - 1);
    }

    @Transactional
    public void addCommission(User beneficiary, User investor, BigDecimal amount, Type type) {
        if (beneficiary.getTotalCommission().add(amount).compareTo(beneficiary.getInvestedAmount().multiply(MAX_COMMISSION_MULTIPLIER)) <= 0) {
            Commission commission = new Commission();
            commission.setInvestor(investor);
            commission.setBeneficiary(beneficiary);
            commission.setType(type);
            commission.setAmount(amount);
            commissionRepository.save(commission);

            beneficiary.getCommissions().add(commission);
            userRepository.save(beneficiary);
        }
    }
/*    private void distributeCommission(User parent, User investor, BigDecimal commissionAmount, Commission.Type type) {
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
    }*/

    @Transactional
    public void calculateMonthlyCommissions() {
        List<User> allUsers = userRepository.findAll();

        for (User user : allUsers) {
            if (user.hasInvestment()) {
                BigDecimal investmentAmount = user.getTotalInvestedAmount(); // Assumes one investment per user
                calculateAndDistributeCommissions(user, investmentAmount);
            }
        }
    }

}
