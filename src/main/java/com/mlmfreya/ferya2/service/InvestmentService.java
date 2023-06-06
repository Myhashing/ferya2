package com.mlmfreya.ferya2.service;

import com.mlmfreya.ferya2.model.Investment;
import com.mlmfreya.ferya2.model.User;
import com.mlmfreya.ferya2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvestmentService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    public User getUserFromInvestment(Investment investment){

        return userRepository.findUserByInvestments(investment);
    }
}
