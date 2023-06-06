package com.mlmfreya.ferya2.service;

import com.mlmfreya.ferya2.model.Investment;
import com.mlmfreya.ferya2.model.User;
import com.mlmfreya.ferya2.repository.InvestmentRepository;
import com.mlmfreya.ferya2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class InterestService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InvestmentRepository investmentRepository;
    @Autowired
    private InvestmentService investmentService;


    public void calculateAndDistributeInterest(Investment investment) {
        // If next interest payment date is today or before today
        if (LocalDate.now().equals(investment.getNextInterestPaymentDate().toLocalDate()) || LocalDate.now().isAfter(investment.getNextInterestPaymentDate().toLocalDate())) {
            BigDecimal interest = investment.getInvestmentPackage().getReturnOnInvestment().multiply(investment.getInvestedAmount());

            User user = investmentService.getUserFromInvestment(investment);
            user.setBalance(user.getBalance().add(interest));
            userRepository.save(user);

            // Add 30 days to next interest payment date
            investment.setNextInterestPaymentDate(investment.getNextInterestPaymentDate().plusDays(30));

            // If next interest payment date is after the end of the contract, set it to null
            if (investment.getNextInterestPaymentDate().isAfter(investment.getInvestmentDate().plusDays(investment.getInvestmentPackage().getDuration()))) {
                investment.setNextInterestPaymentDate(null);
            }
            investmentRepository.save(investment);
        }
    }

}
