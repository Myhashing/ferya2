package com.mlmfreya.ferya2.controller;


import com.mlmfreya.ferya2.model.PurchaseRequestEarly;
import com.mlmfreya.ferya2.model.VpnProduct;
import com.mlmfreya.ferya2.repository.PurchaseRequestRepository;
import com.mlmfreya.ferya2.repository.VpnProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("public/vpn")
public class VpnProductController {

    @Autowired
    private VpnProductRepository vpnProductRepository;

    @Autowired
    private PurchaseRequestRepository purchaseRequestRepository;

    @GetMapping("/vpn-products")
    public Iterable<VpnProduct> getAllVpnProducts() {
        return vpnProductRepository.findAll();
    }

    @GetMapping("early-bird")
    public String earlyBird(Model model){
        List<VpnProduct> products = vpnProductRepository.findAll();
        model.addAttribute("products",products);
        return "vpn/early-bird";
    }

    @PostMapping("/purchase-request")
    public String createPurchaseRequest(@ModelAttribute PurchaseRequestEarly purchaseRequestEarly) {

         purchaseRequestRepository.save(purchaseRequestEarly);

         return "redirect:/dashboard";
    }
}