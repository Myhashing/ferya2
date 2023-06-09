package com.mlmfreya.ferya2.controller;

import com.mlmfreya.ferya2.model.Commission;
import com.mlmfreya.ferya2.model.User;
import com.mlmfreya.ferya2.service.CommissionService;
import com.mlmfreya.ferya2.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final CommissionService commissionService;
    private final UserService userService;

    @Autowired
    public AdminController(CommissionService commissionService, UserService userService) {

        this.commissionService = commissionService;
        this.userService = userService;
    }

    @GetMapping("/pendingCommissions")
    public String getPendingCommissions(Model model) {
        List<Commission> pendingCommissions = commissionService.getPendingCommissions();
        model.addAttribute("pendingCommissions", pendingCommissions);
        return "pendingCommissions"; // This should be the name of your Thymeleaf template
    }

    @PostMapping("/payoutCommission/{commissionId}")
    public String payoutCommission(@PathVariable Long commissionId, Model model) {
        commissionService.payoutCommission(commissionId);
        return "redirect:/admin/pendingCommissions"; // Redirect to the list of pending commissions after payout
    }

    @RequestMapping("/direct-commission")
    public String showDirectCommission(Model model) {
        List<User> eligibleUsers = userService.findUsersEligibleForDirectCommission();
        model.addAttribute("eligibleUsers", eligibleUsers);
        return "admin/direct-commission";
    }


    @PostMapping("/payCommission")
    @ResponseBody
    public String payCommission(@RequestParam("userId") Long userId) {
        try {
            User user = userService.findUserById(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found")); // fetch the user from the database
            if (user != null) {

                commissionService.calculateAndDistributeCommissions(user);
                return "Commission paid successfully for user with ID: " + userId;
            } else {
                return "User not found with ID: " + userId;
            }
        } catch (Exception e) {
            return "Error occurred while paying commission: " + e.getMessage();
        }
    }




}
