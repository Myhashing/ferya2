package com.mlmfreya.ferya2.controller;

import com.mlmfreya.ferya2.model.User;
import com.mlmfreya.ferya2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String profile(Model model, Principal principal) {
        String email = principal.getName(); // Get logged in username
        User user = userService.findByUsername(email);
        model.addAttribute("user",user);
        return "profile"; // return profile view
    }
}
