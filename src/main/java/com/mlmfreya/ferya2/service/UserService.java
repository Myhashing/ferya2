package com.mlmfreya.ferya2.service;


import com.mlmfreya.ferya2.dto.UserRegistrationDto;
import com.mlmfreya.ferya2.model.*;
import com.mlmfreya.ferya2.repository.CommissionRepository;
import com.mlmfreya.ferya2.repository.InvestmentRepository;
import com.mlmfreya.ferya2.repository.PayoutRepository;
import com.mlmfreya.ferya2.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final EntityManager entityManager;


    @Autowired
    public UserService(UserRepository userRepository, EntityManager entityManager) {
        this.userRepository = userRepository;
        this.entityManager = entityManager;

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
    private EmailService emailService;
    @Autowired
    private CommissionRepository commissionRepository;
    @Autowired
    private PayoutRepository payoutRepository;

    public Investment getUserInvestments(User user) {
        // Get the user from the database (you might already have the user entity, in that case you don't need this line)
        User userFromDb = userRepository.findById(user.getId()).orElse(null);

        if (userFromDb != null) {
            return userFromDb.getInvestments();
        } else {
            // Handle the case when the user is not found in the database
            return null;
        }
    }


    public List<Commission> getUserCommissions(User user) {
        return commissionRepository.findByBeneficiary(user);
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
        if (user.getRole() == null){
            user.setRole(User.Role.USER);
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

        // If not, save the user
        return userRepository.save(user);
    }


    public User registerUser(User user, User parent, String position) {
        user.setRole(User.Role.USER);
        user.setParent(parent);
        user.setBalance(BigDecimal.ZERO);

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



    public void sendPasswordResetEmail(User user) throws IOException {
        String token = UUID.randomUUID().toString();
        user.setResetPasswordToken(token);
        userRepository.save(user);

        String resetPasswordLink = "/reset-password?token=" + token;

        emailService.sendVerificationEmail(user,resetPasswordLink);
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

    public void addPackageToUser(User user, InvestmentPackage investmentPackage, BigDecimal investedAmount, User networkRootUser) {
        Investment investment = new Investment();
        investment.setInvestmentPackage(investmentPackage);
        investment.setInvestedAmount(investedAmount);
        investment.setInvestmentDate(LocalDateTime.now());
        investment.setNextInterestPaymentDate(LocalDateTime.now().plusDays(30));
        investment.setNetworkRootUser(networkRootUser);

        investmentRepository.save(investment);

        user.setInvestments(investment);
        userRepository.save(user);

        // Calculate and distribute commissions after a new package is added to the user
//        commissionService.calculateAndDistributeCommissions(user, investedAmount);
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
        Investment investment = user.getInvestments();
        BigDecimal totalInterest = BigDecimal.ZERO;

        if (investment != null) {
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
        Investment investment = getUserInvestments(user);
        BigDecimal total = BigDecimal.ZERO;
        if (investment != null){
            total = investment.getInvestedAmount();
        }

        return total;
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

    public Investment getInvestmentsInNetwork(User user) {
       Investment investments = new Investment();
        addInvestmentsInNetwork(user, investments);
        return investments;
    }

    private void addInvestmentsInNetwork(User user, Investment investments) {
        investments = user.getInvestments();

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
    public List<User> getAllChildren(User user) {
        return entityManager.createQuery(
                        "SELECT u FROM User u LEFT JOIN FETCH u.investments WHERE u.parent = :parent", User.class)
                .setParameter("parent", user)
                .getResultList();
    }


    private void getAllChildrenHelper(User user, List<User> children) {
        if (user == null) {
            return;
        }
        if (user.getLeftChild() != null) {
            children.add(user.getLeftChild());
            getAllChildrenHelper(user.getLeftChild(), children);
        }
        if (user.getRightChild() != null) {
            children.add(user.getRightChild());
            getAllChildrenHelper(user.getRightChild(), children);
        }
    }

    public User getInvestmentInNetwork(User user, User networkRootUser) {
        Investment investment = user.getInvestments();
        if (investment != null && investment.getNetworkRootUser().equals(networkRootUser)) {
            return user;
        } else {
            throw new RuntimeException("No investment found for this user with the specified network root user.");
        }
    }


    public List<User> findUsersEligibleForDirectCommission() {
        return userRepository.findUsersEligibleForDirectCommission();
    }

    public Optional<User> findUserById(Long userId) {
        return userRepository.findById(userId);
    }


    public long countUsers() {
        return userRepository.count();
    }
}
