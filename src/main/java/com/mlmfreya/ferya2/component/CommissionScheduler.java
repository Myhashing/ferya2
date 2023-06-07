package com.mlmfreya.ferya2.component;

import com.mlmfreya.ferya2.model.User;
import com.mlmfreya.ferya2.repository.UserRepository;
import com.mlmfreya.ferya2.service.CommissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommissionScheduler {



    @Autowired
    private CommissionService commissionService;

    @Scheduled(cron = "0 0 0 1 * ?") // This means the job will run at 00:00 on the 1st day of every month.
    public void calculateMonthlyCommissions() {
        commissionService.calculateMonthlyCommissions();

    }
}