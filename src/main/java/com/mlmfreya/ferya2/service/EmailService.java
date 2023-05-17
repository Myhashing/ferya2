package com.mlmfreya.ferya2.service;

import com.mlmfreya.ferya2.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(User user, String siteURL) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Email Verification");
        message.setText("To verify your email address, please click the link below:\n"
                + siteURL + "/verify?token=" + user.getEmailVerificationToken());
        mailSender.send(message);
    }


    public void sendWelcomeEmail(User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Welcome to our service!");
        message.setText("To verify your email address, please click the link below:\n");
        mailSender.send(message);
    }
}
