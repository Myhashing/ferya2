package com.mlmfreya.ferya2.controller;

import com.mlmfreya.ferya2.model.User;
import com.mlmfreya.ferya2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class DashboardController {

    private final UserService userService;

    @Autowired
    public DashboardController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/dashboard")
    public String showDashboard(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("investments", userService.getUserInvestments(user));
        model.addAttribute("commissions", userService.getUserCommissions(user));
        model.addAttribute("payouts", userService.getPayoutHistory(user));
        model.addAttribute("user", user); // User entity is added to the model
        return "dashboard";
    }


    @PostMapping("/withdraw")
    public String withdrawIncome(@RequestParam("amount") double amount,
                                 @RequestParam("account") String account,
                                 Principal principal) {
        User user = userService.findByUsername(principal.getName());
        userService.requestWithdraw(user, amount, account);
        return "redirect:/dashboard";
    }

    @PostMapping("/profile")
    public String updateProfile(@RequestParam("email") String email,
                                @RequestParam("name") String name,
                                Principal principal,
                                // Add other @RequestParam annotations for other fields
                                Model model) {
        // Check if the logged in user email matches the email parameter
        if(principal.getName().equals(email)) {
            userService.updateUserProfile(email, name /* other fields */);
            return "redirect:/dashboard";
        }
        // If the email parameter does not match the logged in user, return an error view
        model.addAttribute("error", "Cannot update profile of another user");
        return "error";
    }

}
