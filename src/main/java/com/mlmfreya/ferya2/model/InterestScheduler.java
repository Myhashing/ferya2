package com.mlmfreya.ferya2.model;

import com.mlmfreya.ferya2.repository.InvestmentRepository;
import com.mlmfreya.ferya2.service.InterestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InterestScheduler {
    @Autowired
    private InterestService interestService;

    @Autowired
    private InvestmentRepository investmentRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void scheduleInterestPayment() {
        List<Investment> investments = investmentRepository.findAll();

        for (Investment investment : investments) {
            interestService.calculateAndDistributeInterest(investment);
        }
    }
}
