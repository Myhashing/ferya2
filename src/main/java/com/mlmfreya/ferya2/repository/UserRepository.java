package com.mlmfreya.ferya2.repository;

import com.mlmfreya.ferya2.model.Investment;
import com.mlmfreya.ferya2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    User findByEmailVerificationToken(String token);

    Boolean existsByReferralCode(String referralCode);

    User findByReferralCode(String referralCode);

    @Query("SELECT u FROM User u JOIN FETCH u.investments WHERE u.investments = :investment")
    User findUserByInvestments(@Param("investment") Investment investment);


    List<User> findByParent(User parent);

    @Query("SELECT u FROM User u WHERE u.totalReferralCommission = 0 AND u.leftChild != null AND u.rightChild != null")
    List<User> findUsersEligibleForDirectCommission();

    @Query("SELECT u FROM User u JOIN FETCH u.investments WHERE u.id = :id")
    Optional<User> findByIdWithInvestments(@Param("id") Long id);
}