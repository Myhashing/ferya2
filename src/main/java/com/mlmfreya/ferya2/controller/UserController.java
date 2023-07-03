package com.mlmfreya.ferya2.controller;


import com.mlmfreya.ferya2.dto.UserRegistrationDto;
import com.mlmfreya.ferya2.model.User;
import com.mlmfreya.ferya2.model.WithdrawRequest;
import com.mlmfreya.ferya2.repository.UserRepository;
import com.mlmfreya.ferya2.service.UserDetailsServiceImpl;
import com.mlmfreya.ferya2.service.UserService;
import com.mlmfreya.ferya2.service.WithdrawService;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.security.Principal;
import java.util.List;

@Controller
public class UserController {
    private final UserService userService;
    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    private final WithdrawService withdrawService;

    @Autowired
    public UserController(UserService userService,
                          UserDetailsServiceImpl userDetailsService,
                          PasswordEncoder passwordEncoder,
                          UserRepository userRepository,
                          WithdrawService withdrawService) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.withdrawService = withdrawService;
    }

    @GetMapping("/users/{email}")
    public String getUser(@PathVariable String email, Model model) {
        User user = userService.getUserByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        model.addAttribute("user", user);
        return "user"; // This should be the name of your user view

    }

/*    @GetMapping("/register")
    public ModelAndView registerForm() {
        return new ModelAndView("register", "user", new User());
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {
        user.setRole(User.Role.ADMIN);

        userService.registerUser(user);
        return "redirect:/login";
    }*/

    @GetMapping("/public/forgot-password")
    public String forgetpassword(){
        return "forget-password";
    }

    @PostMapping("/public/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        User user = userService.findByEmail(email);
        if (user == null) {
            model.addAttribute("message", "We couldn't find an account for that e-mail address.");
        } else {
            userService.sendPasswordResetEmail(user);
            model.addAttribute("message", "We have sent a password reset link to your email.");
        }
        return "forgot-password";
    }

    @GetMapping("/verify")
    public String verifyUser(@RequestParam("token") String token, Model model) {
        if (userService.verifyEmailToken(token)) {
            model.addAttribute("message", "Email successfully verified.");
            return "message";
        } else {
            model.addAttribute("message", "Invalid or expired email verification token.");
            return "message";
        }
    }

    //create login endpoint and then create second login endpoint for receive the form data and check if user authenticated

    @GetMapping("/login")
    public String login() {
        return "signin2";}




    @GetMapping("/public/checkEmailUnique")
    @ResponseBody
    public boolean checkEmailUnique(@RequestParam String email) {
        return userService.isEmailUnique(email);
    }



    @GetMapping("/public/referralCodeExists")
    @ResponseBody
    public boolean referralCodeExists(@RequestParam String referralCode) {
        User parent = userService.getUserByReferralCode(referralCode);
        if (parent != null) {
            // Check if either left or right child is empty
            return (parent.getLeftChild() == null || parent.getRightChild() == null);
        } else {
            return false;
        }
    }



    @GetMapping("/statements")
    public String statements( Principal principal,Model model ){
        try {

            if (principal != null) {
                User user = userService.findByUsername(principal.getName());
                if (user != null) {
                    model.addAttribute("user", user);
                    List<WithdrawRequest> withdrawRequest = withdrawService.all(user);
                    model.addAttribute("withdraws", withdrawRequest);
                }
            }
            return "dashboard/pages/withdraw";

        }catch(Exception e){
            model.addAttribute("error",e.getMessage());
            return "redirect:/withdraw?error";
        }

    }


    @GetMapping("/withdraw")
    public String withdralist( Principal principal,Model model ){
        try {

            if (principal != null) {
                User user = userService.findByUsername(principal.getName());
                if (user != null) {
                    model.addAttribute("user", user);
                    List<WithdrawRequest> withdrawRequest = withdrawService.all(user);
                    model.addAttribute("withdraws", withdrawRequest);
                }
            }
            return "dashboard/pages/withdraw";

        }catch(Exception e){
            model.addAttribute("error",e.getMessage());
            return "redirect:/withdraw?error";
        }

    }

    @GetMapping("/withdraw/request")
    public String withdrawRequestForm(Principal principal, Model model, RedirectAttributes redirectAttrs){
            if (principal != null) {
                User user = userService.findByUsername(principal.getName());
                if (user != null) {
                    model.addAttribute("user", user);

                }
            }
        if (redirectAttrs.getFlashAttributes().containsKey("error")) {
            model.addAttribute("error", redirectAttrs.getFlashAttributes().get("error"));
        }
            return "dashboard/pages/withdraw-request";
    }

    @PostMapping("/withdraw")
    public String withdraw(@RequestParam("amount") BigDecimal amount, Principal principal, RedirectAttributes redirectAttrs) {
        try {
            User user = userService.getUserByEmail(principal.getName()).orElseThrow(() -> new UsernameNotFoundException(" User Not found"));
            if (user.getBalance().compareTo(BigDecimal.valueOf(30)) >= 0 && user.getBalance().compareTo(amount) >= 0) {
                withdrawService.create(amount, user);
                return "redirect:/withdraw?success";
            } else {
                redirectAttrs.addFlashAttribute("error", "Insufficient balance or invalid amount.");
                return "redirect:/withdraw/request";
            }
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Insufficient balance or invalid amount.");
            return "redirect:/withdraw/request";
        }
    }










}