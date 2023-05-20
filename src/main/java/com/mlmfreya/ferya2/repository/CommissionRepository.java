package com.mlmfreya.ferya2.repository;

import com.mlmfreya.ferya2.model.Commission;
import com.mlmfreya.ferya2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommissionRepository extends JpaRepository<Commission, Long> {
    List<Commission> findByUser(User user);
}