package com.mlmfreya.ferya2.service;

import com.mlmfreya.ferya2.model.Investment;
import com.mlmfreya.ferya2.model.User;
import com.mlmfreya.ferya2.repository.InvestmentRepository;
import com.mlmfreya.ferya2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class InvestmentService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InvestmentRepository investmentRepository;

    public User getUserFromInvestment(Investment investment){

        return userRepository.findUserByInvestments(investment);
    }


    public BigDecimal countInvestments() {
        return investmentRepository.sumInvestedAmount();
    }
}
