package com.mlmfreya.ferya2.service;

import com.mlmfreya.ferya2.model.User;
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {

    @Value("${sendgrid.api.key}")
    private String sendGridAPIKey;

    @Async("taskExecutor")
    public void sendVerificationEmail(User user, String siteURL) throws IOException {
        Email from = new Email("info@coinnich.com"); // replace with your email
        String subject = "Email Verification";
        Email to = new Email(user.getEmail());
        Content content = new Content("text/plain", "To verify your email address, please click the link below:\n"
                + siteURL + "/verify?token=" + user.getEmailVerificationToken());
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(sendGridAPIKey);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        sg.api(request);
    }

    @Async("taskExecutor")
    public void sendWelcomeEmail(User user) throws IOException {
        Email from = new Email("info@coinnich.com"); // replace with your email
        String subject = "Welcome to our service!";
        Email to = new Email(user.getEmail());
        Content content = new Content("text/plain", "Welcome to our service!");
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(sendGridAPIKey);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        sg.api(request);
    }
}
