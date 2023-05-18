package com.mlmfreya.ferya2.service;


import com.mlmfreya.ferya2.dto.UserRegistrationDto;
import com.mlmfreya.ferya2.model.InvestmentPackage;
import com.mlmfreya.ferya2.model.User;
import com.mlmfreya.ferya2.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
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

    public User registerUser(UserRegistrationDto userRegistrationDto) {
        ModelMapper modelMapper = new ModelMapper();

        User user = modelMapper.map(userRegistrationDto, User.class);
        return registerUser(user);


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
        user.getInvestmentPackages().add(investmentPackage);
        user.getInvestedAmounts().add(investedAmount);
        userRepository.save(user);
    }

    public boolean isEmailUnique(String email) {
        return userRepository.findByEmail(email) == null;
    }


}
