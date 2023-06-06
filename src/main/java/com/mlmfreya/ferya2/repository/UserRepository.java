package com.mlmfreya.ferya2.repository;

import com.mlmfreya.ferya2.model.Investment;
import com.mlmfreya.ferya2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    User findByEmailVerificationToken(String token);

    Boolean existsByReferralCode(String referralCode);

    User findByReferralCode(String referralCode);

    User findUserByInvestments(Investment investment);

    List<User> findByParent(User parent);
}