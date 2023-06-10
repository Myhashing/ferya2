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



    @GetMapping("/dashboard")
    public String ShowCrypto(Model model,Principal principal){

        if (principal != null) {
            User user = userService.findByUsername(principal.getName());
            if (user != null) {
                model.addAttribute("user", user);
                List<User> children = userService.getAllChildren(user);
                model.addAttribute("children", children);
            }
        }
        model.addAttribute("btcprice",binanceService.CryptoPrice("BTCUSDT"));
        model.addAttribute("ethprice",binanceService.CryptoPrice("ETHUSDT"));
        model.addAttribute("dogeprice",binanceService.CryptoPrice("DOGEUSDT"));
        model.addAttribute("trxprice",binanceService.CryptoPrice("TRXUSDT"));
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
                model.addAttribute("netEarnings", userService.getTotalEarnings(user));
                model.addAttribute("totalCommissions", userService.getTotalCommissions(user));
                model.addAttribute("totalInvestments", userService.getTotalInvestments(user));
                model.addAttribute("totalUserNetwork", userService.getTotalUserNetwork(user));
                model.addAttribute("totalMonthlyInterest", userService.getTotalInterestPerMonth(user)); // total monthly interest received
                model.addAttribute("referralSignups", children.size());
                model.addAttribute("avgInvestment", userService.getAverageInvestmentPerUserInNetwork(user));

            }
        }
        return "dashboard/pages/referrals";
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
