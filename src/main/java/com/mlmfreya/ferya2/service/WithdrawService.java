package com.mlmfreya.ferya2.service;

import com.mlmfreya.ferya2.model.User;
import com.mlmfreya.ferya2.model.WithdrawRequest;
import com.mlmfreya.ferya2.repository.UserRepository;
import com.mlmfreya.ferya2.repository.WithdrawRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.LimitExceededException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WithdrawService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WithdrawRequestRepository withdrawRequestRepository;

    @Value("${WithdrawLowLimit}")
    private BigDecimal withdrawLowLimit;

    @Value("${WithdrawHighLimit}")
    private BigDecimal withdrawHighLimit;



    @Transactional
    public WithdrawRequest create(BigDecimal amount, User user) throws LimitExceededException {
//TODO: add the high withdraw limit
        if (user.getBalance().compareTo(withdrawLowLimit) >= 0 && amount.compareTo(withdrawLowLimit) >= 0) {
            WithdrawRequest withdrawRequest = new WithdrawRequest();
            withdrawRequest.setRequestDate(LocalDateTime.now());
            withdrawRequest.setUser(user);
            withdrawRequest.setAmount(amount);
            withdrawRequest.setStatus(WithdrawRequest.Status.PENDING);
            withdrawRequestRepository.save(withdrawRequest);
            user.setBalance(user.getBalance().subtract(amount));
            userRepository.save(user);
            return withdrawRequest;
        }else {
            throw new LimitExceededException("You have to submit request in the limit range");
        }
        }

    public List<WithdrawRequest> all(User user) {
        return withdrawRequestRepository.findWithdrawRequestsByUser(user);
    }
}
