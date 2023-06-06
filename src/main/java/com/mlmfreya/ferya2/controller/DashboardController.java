package com.mlmfreya.ferya2.controller;

import com.mlmfreya.ferya2.model.Investment;
import com.mlmfreya.ferya2.model.User;
import com.mlmfreya.ferya2.service.BinanceService;
import com.mlmfreya.ferya2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DashboardController {

    private final UserService userService;
    private final BinanceService binanceService;

    @Autowired
    public DashboardController(UserService userService,
                               BinanceService binanceService) {
        this.userService = userService;
        this.binanceService = binanceService;
    }


    @GetMapping("/das")
    public String showDashboard(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("investments", userService.getUserInvestments(user));
        model.addAttribute("commissions", userService.getUserCommissions(user));
        model.addAttribute("payouts", userService.getPayoutHistory(user));
        model.addAttribute("user", user); // User entity is added to the model
        return "dashboard/dashboard";
    }

    @GetMapping("/dashboard")
    public String ShowCrypto(Model model,Principal principal){

        if (principal != null) {
            User user = userService.findByUsername(principal.getName());
            if (user != null) {
                model.addAttribute("user", user);
            }
        }
        model.addAttribute("btcprice",binanceService.CryptoPrice("BTCUSDT"));
        model.addAttribute("ethprice",binanceService.CryptoPrice("ETHUSDT"));
        model.addAttribute("dogeprice",binanceService.CryptoPrice("DOGEUSDT"));
        return "dashboard/default";
    }

    @GetMapping("/overview")
    public String ShowOverview(Model model, Principal principal) {
        User user = null;
        if (principal != null) {
            user = userService.findByUsername(principal.getName());
            if (user != null) {
                model.addAttribute("user", user);
                model.addAttribute("totalEarnings", userService.getTotalEarnings(user));
                    model.addAttribute("totalCommissions", userService.getTotalCommissions(user));
                    model.addAttribute("totalInvestments", userService.getTotalInvestments(user));
                    model.addAttribute("totalUserNetwork", userService.getTotalUserNetwork(user));
                    model.addAttribute("totalInvestedAmount", userService.getTotalInvestments(user)); // total invested amount
                    model.addAttribute("totalMonthlyInterest", userService.getTotalInterestPerMonth(user)); // total monthly interest received
                }
            }
            model.addAttribute("investments", userService.getUserInvestments(user));
            model.addAttribute("commissions", userService.getUserCommissions(user));
            model.addAttribute("payouts", userService.getPayoutHistory(user));
            return "dashboard/pages/overview";
        }

    @GetMapping("/referral")
    public String showReferralDetails(Model model, Principal principal) {
        User user = null;
        if (principal != null) {
            user = userService.findByUsername(principal.getName());
            if (user != null) {
                model.addAttribute("user", user);
                List<User> children = userService.getAllChildren(user);
                model.addAttribute("children", children);
                model.addAttribute("totalEarnings", userService.getTotalEarnings(user));
                model.addAttribute("totalCommissions", userService.getTotalCommissions(user));
                model.addAttribute("totalInvestments", userService.getTotalInvestments(user));
                model.addAttribute("totalUserNetwork", userService.getTotalUserNetwork(user));
                model.addAttribute("totalInvestedAmount", userService.getTotalInvestments(user)); // total invested amount
                model.addAttribute("totalMonthlyInterest", userService.getTotalInterestPerMonth(user)); // total monthly interest received
                model.addAttribute("referralSignups", userService.getTotalUserNetwork(user));
                model.addAttribute("avgInvestment", userService.getAverageInvestmentPerUserInNetwork(user));
                model.addAttribute("netEarnings", userService.getTotalEarnings(user));
                List<Investment> investments = userService.getInvestmentsInNetwork(user);
                model.addAttribute("investments", investments);
                Map<User, Investment> childInvestments = new HashMap<>();
                for (User child : children) {
                    Investment investment = userService.getInvestmentInNetwork(child, user);
                    childInvestments.put(child, investment);
                }
                model.addAttribute("childInvestments", childInvestments);

            }
        }
        return "dashboard/pages/referrals";
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
