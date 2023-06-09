package com.mlmfreya.ferya2.repository;

import com.mlmfreya.ferya2.model.User;
import com.mlmfreya.ferya2.model.WithdrawRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WithdrawRequestRepository extends JpaRepository<WithdrawRequest, Long> {


    List<WithdrawRequest> findWithdrawRequestsByUser(User user);
}