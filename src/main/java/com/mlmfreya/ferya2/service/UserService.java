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
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.math.BigDecimal;

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
        return commissionRepository.findByInvestor(user);
    }

    public List<Payout> getPayoutHistory(User user) {
        return payoutRepository.findByUser(user);
    }



    public void requestWithdraw(User user, double amount, String account) {
        // Implement your withdrawal logic here
        // You might want to create a new Withdrawal model and repository
        // And save the request there for later processing
    }

    public void updateUserProfile(String email, String name) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setFullName(name);
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

        String resetPasswordLink = "/reset-password?token=" + token;

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

    public BigDecimal getTotalInterestPerMonth(User user) {
        List<Investment> investments = getUserInvestments(user);
        BigDecimal totalInterest = BigDecimal.ZERO;

        for (Investment investment : investments) {
            BigDecimal returnRate = investment.getInvestmentPackage().getReturnOnInvestment().divide(new BigDecimal(100));
            BigDecimal investmentAmount = investment.getInvestedAmount();
            BigDecimal interest = investmentAmount.multiply(returnRate);
            totalInterest = totalInterest.add(interest);
        }

        return totalInterest;
    }


    public BigDecimal getTotalCommissions(User user) {
        List<Commission> commissions = getUserCommissions(user);
        BigDecimal totalCommissions = BigDecimal.ZERO;

        for (Commission commission : commissions) {
            totalCommissions = totalCommissions.add(commission.getAmount());
        }

        return totalCommissions;
    }


    public BigDecimal getTotalInvestments(User user) {
        List<Investment> investments = getUserInvestments(user);
        BigDecimal totalInvestments = BigDecimal.ZERO;

        for (Investment investment : investments) {
            totalInvestments = totalInvestments.add(investment.getInvestedAmount());
        }

        return totalInvestments;
    }


    public int getTotalUserNetwork(User user) {
        int totalNetwork = 0;

        User leftChild = user.getLeftChild();
        User rightChild = user.getRightChild();

        if (leftChild != null) {
            totalNetwork += 1 + getTotalUserNetwork(leftChild);
        }

        if (rightChild != null) {
            totalNetwork += 1 + getTotalUserNetwork(rightChild);
        }

        return totalNetwork;
    }

    public BigDecimal getTotalEarnings(User user) {
        BigDecimal totalCommissions = getTotalCommissions(user);
        BigDecimal totalMonthlyInterest = getTotalInterestPerMonth(user);
        return totalCommissions.add(totalMonthlyInterest);
    }



    public BigDecimal getAverageInvestmentPerUserInNetwork(User user) {
        int totalUserNetwork = getTotalUserNetwork(user);
        BigDecimal totalInvestmentsInNetwork = getTotalInvestmentsInNetwork(user);

        if (totalUserNetwork == 0) {
            return BigDecimal.ZERO;
        }

        return totalInvestmentsInNetwork.divide(new BigDecimal(totalUserNetwork), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotalInvestmentsInNetwork(User user) {
        BigDecimal totalInvestments = getTotalInvestments(user);

        User leftChild = user.getLeftChild();
        User rightChild = user.getRightChild();

        if (leftChild != null) {
            totalInvestments = totalInvestments.add(getTotalInvestmentsInNetwork(leftChild));
        }

        if (rightChild != null) {
            totalInvestments = totalInvestments.add(getTotalInvestmentsInNetwork(rightChild));
        }

        return totalInvestments;
    }

    public List<Investment> getInvestmentsInNetwork(User user) {
        List<Investment> investments = new ArrayList<>();
        addInvestmentsInNetwork(user, investments);
        return investments;
    }

    private void addInvestmentsInNetwork(User user, List<Investment> investments) {
        investments.addAll(user.getInvestments());

        User leftChild = user.getLeftChild();
        User rightChild = user.getRightChild();

        if (leftChild != null) {
            addInvestmentsInNetwork(leftChild, investments);
        }

        if (rightChild != null) {
            addInvestmentsInNetwork(rightChild, investments);
        }
    }

    public List<User> getUsersInNetwork(User user, int level) {
        List<User> usersInNetwork = new ArrayList<>();
        if (level == 0) {
            usersInNetwork.add(user);
        }
        if (level < 15) {
            List<User> referredUsers = getReferredUsers(user);
            for (User referredUser : referredUsers) {
                usersInNetwork.add(referredUser);
                usersInNetwork.addAll(getUsersInNetwork(referredUser, level + 1));
            }
        }
        return usersInNetwork;
    }


    public List<User> getReferredUsers(User user) {
        List<User> referredUsers = new ArrayList<>();
        addReferredUsers(user, referredUsers);
        return referredUsers;
    }

    private void addReferredUsers(User user, List<User> referredUsers) {
        if (user == null) {
            return;
        }

        referredUsers.add(user);

        addReferredUsers(user.getLeftChild(), referredUsers);
        addReferredUsers(user.getRightChild(), referredUsers);
    }






}
