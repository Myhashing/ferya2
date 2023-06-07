package com.mlmfreya.ferya2.controller;

import com.mlmfreya.ferya2.model.Commission;
import com.mlmfreya.ferya2.service.CommissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final CommissionService commissionService;

    @Autowired
    public AdminController(CommissionService commissionService) {
        this.commissionService = commissionService;
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
}
