package com.mlmfreya.ferya2.service;

import com.mlmfreya.ferya2.model.*;
import com.mlmfreya.ferya2.repository.InvestmentHistoryRepository;
import com.mlmfreya.ferya2.repository.InvestmentRepository;
import com.mlmfreya.ferya2.repository.PaymentRequestRepository;
import com.mlmfreya.ferya2.repository.PaymentRequestUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class InterPaymentWatcherService {
    @Autowired
    private PaymentRequestUserRepository paymentRequestUserRepository;
    @Autowired
    private TronWebService tronWebService;
    @Autowired
    private PaymentRequestRepository paymentRequestRepository;
    @Autowired
    private InvestmentHistoryRepository investmentHistoryRepository;

    @Autowired
    private InvestmentRepository investmentRepository;
    public void watcher(PaymentRequestUser paymentRequestUser){
        new Thread(()->{
            boolean paymentReceived = false;
            while (!paymentReceived){
                try{
                    BigDecimal balance = tronWebService.checkWalletBalance(paymentRequestUser.getWallet().getBase58()).divide(BigDecimal.valueOf(1000000));
                    if (balance.compareTo(paymentRequestUser.getAmount()) >= 0){
                        process(paymentRequestUser);
                        paymentReceived = true;
                    }
                }catch (RuntimeException e){
                    e.printStackTrace();
                    System.out.println("Error while checking wallet balance, retrying in 60 seconds...");
                    try {
                        Thread.sleep(60000);

                    }catch(InterruptedException ie){
                        ie.printStackTrace();
                    }
                    continue;
                }
                try {
                    Thread.sleep(5000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Transactional
    public void process(PaymentRequestUser paymentRequestUser) {
        //save investment history x 2
        InvestmentHistory investmentHistory = new InvestmentHistory();
        investmentHistory.setInvestment(paymentRequestUser.getUser().getInvestments());
        investmentHistory.setAdditionalAmount(paymentRequestUser.getUser().getInvestedAmount());
        investmentHistory.setDate(paymentRequestUser.getUser().getCreatedAt());
        investmentHistoryRepository.save(investmentHistory);

        InvestmentHistory investmentHistory1 = new InvestmentHistory();
        investmentHistory1.setInvestment(paymentRequestUser.getUser().getInvestments());
        investmentHistory1.setDate(LocalDateTime.now());
        investmentHistory1.setAdditionalAmount(paymentRequestUser.getAmount());
        investmentHistoryRepository.save(investmentHistory1);
        //add to user's investment balance
        Investment investment = paymentRequestUser.getUser().getInvestments();
        investment.setInvestedAmount(paymentRequestUser.getAmount().add(paymentRequestUser.getUser().getInvestedAmount()));
        investmentRepository.save(investment);
        //update the payment request status
        paymentRequestUser.setStatus(PaymentRequestUser.Status.Paid);
        paymentRequestUserRepository.save(paymentRequestUser);

    }
}
