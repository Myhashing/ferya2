package com.mlmfreya.ferya2.service;


import com.mlmfreya.ferya2.dto.UserRegistrationDto;
import com.mlmfreya.ferya2.model.*;
import com.mlmfreya.ferya2.repository.CommissionRepository;
import com.mlmfreya.ferya2.repository.InvestmentRepository;
import com.mlmfreya.ferya2.repository.PayoutRepository;
import com.mlmfreya.ferya2.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private CommissionService commissionService;
    @Autowired
    private InvestmentRepository investmentRepository;


    @Autowired
    private CommissionRepository commissionRepository;
    @Autowired
    private PayoutRepository payoutRepository;

    public List<Investment> getUserInvestments(User user) {
        return investmentRepository.findByUser(user);
    }

    public List<Commission> getUserCommissions(User user) {
        return commissionRepository.findByUser(user);
    }

    public List<Payout> getPayoutHistory(User user) {
        return payoutRepository.findByUser(user);
    }

    public UserProfile getUserProfile(User user) {
        return userProfileRepository.findByUser(user);
    }



    public void requestWithdraw(User user, double amount, String account) {
        // Implement your withdrawal logic here
        // You might want to create a new Withdrawal model and repository
        // And save the request there for later processing
    }

    public void updateUserProfile(String email, String name, /* other fields */) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setName(name);
        // Update other fields...
        userRepository.save(user);
    }
    public User registerUser(UserRegistrationDto userRegistrationDto, User parent) {
        ModelMapper modelMapper = new ModelMapper();

        User user = modelMapper.map(userRegistrationDto, User.class);
        String position;
        if (parent.getLeftChild() == null) {
            position = "LEFT";
        } else if (parent.getRightChild() == null) {
            position = "RIGHT";
        } else {
            throw new IllegalArgumentException("Parent user already has two children");
        }
        return registerUser(user,parent,position);


    }
    public User registerUser(User user) {
        user.setRole(User.Role.USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use");
        }

        // If not, save the user
        return userRepository.save(user);
    }


    public User registerUser(User user, User parent, String position) {
        user.setRole(User.Role.USER);
        user.setParent(parent);

        if (position.equals("LEFT")) {
            parent.setLeftChild(user);
        } else if (position.equals("RIGHT")) {
            parent.setRightChild(user);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use");
        }
        String referralCode  ;
        do {
            referralCode = String.format("%06d", new Random().nextInt(999999));
        } while (referralCodeExists(referralCode));
        user.setReferralCode(referralCode);


        User newUser = userRepository.save(user);
        // If not, save the user
        userRepository.save(parent);
        return newUser;
    }


    public boolean referralCodeExists(String referralCode){
       return userRepository.existsByReferralCode(referralCode);

    }

    public User getUserByReferralCode(String referralCode) {
        return userRepository.findByReferralCode(referralCode);
    }




    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findByUsername(String username) {
        return userRepository.findByEmail(username).orElseThrow(()-> new UsernameNotFoundException("User not found"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not found"));
    }



    public void sendPasswordResetEmail(User user) {
        String token = UUID.randomUUID().toString();
        user.setResetPasswordToken(token);
        userRepository.save(user);

        String resetPasswordLink = "http://localhost:8080/reset-password?token=" + token;

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Password Reset Request");
        mailMessage.setText("To complete the password reset process, please click here: " + resetPasswordLink);
        javaMailSender.send(mailMessage);
    }

    public boolean verifyEmailToken(String token) {
        User user = userRepository.findByEmailVerificationToken(token);
        if (user != null && !user.isEmailVerified()) {
            user.setEmailVerified(true);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public void addPackageToUser(User user, InvestmentPackage investmentPackage, BigDecimal investedAmount) {
        Investment investment = new Investment();
        investment.setUser(user);
        investment.setInvestmentPackage(investmentPackage);
        investment.setInvestedAmount(investedAmount);
        investment.setInvestmentDate(LocalDateTime.now());
        investment.setNextInterestPaymentDate(LocalDateTime.now().plusDays(30));
        investmentRepository.save(investment);

        user.getInvestments().add(investment);
        userRepository.save(user);

        // Calculate and distribute commissions after a new package is added to the user
        commissionService.calculateAndDistributeCommissions(user, investedAmount);
    }


    public boolean isEmailUnique(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if(user == null || user.isEmpty()) {
            System.out.println("Email " + email + " is unique.");
            return true;
        } else {
            System.out.println("Email " + email + " already exists.");
            return false;
        }
    }



}
