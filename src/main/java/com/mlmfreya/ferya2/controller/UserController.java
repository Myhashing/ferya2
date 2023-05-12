package com.mlmfreya.ferya2.controller;


import com.mlmfreya.ferya2.model.User;
import com.mlmfreya.ferya2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/{email}")
    public String getUser(@PathVariable String email, Model model) {
        User user = userService.getUserByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        model.addAttribute("user", user);
        return "user"; // This should be the name of your user view

    }

    @GetMapping("/register")
    public ModelAndView registerForm() {
        return new ModelAndView("register", "user", new User());
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {
        userService.registerUser(user);
        return "redirect:/login";
    }

    @PostMapping("/forgot-password")
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
        return "login";}

    //create login post endpoint to receive form data validate and authenticate user






}