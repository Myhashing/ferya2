package com.mlmfreya.ferya2.controller.admin;

import com.mlmfreya.ferya2.model.Wallet;
import com.mlmfreya.ferya2.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class WalletController {

    @Autowired
    private WalletRepository walletRepository;

    @GetMapping("/wallets")
    public String listWallets(Model model) {
        List<Wallet> wallets = walletRepository.findAll();
        model.addAttribute("wallets", wallets);
        return "admin/wallets";
    }
}
