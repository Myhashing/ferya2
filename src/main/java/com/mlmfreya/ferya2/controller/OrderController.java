package com.mlmfreya.ferya2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mlmfreya.ferya2.dto.UserRegistrationDto;
import com.mlmfreya.ferya2.dto.WalletResponse;
import com.mlmfreya.ferya2.model.InvestmentPackage;
import com.mlmfreya.ferya2.model.PaymentRequest;
import com.mlmfreya.ferya2.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

import org.json.JSONObject;


@Controller
public class OrderController {

    @Autowired
    private ShoppingCart cart;

    @Autowired
    private InvestmentPackageService packageService;
    @Autowired
    private PaymentGatewayService paymentService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TronWebService tronWebService;

    @PostMapping("/add-to-cart")
    public String addToCart(@RequestParam("packageId") Long packageId) {
        InvestmentPackage investmentPackage = packageService.getPackage(packageId);
        cart.clear(); // Clear the cart to allow only one item at a time
        cart.addPackage(investmentPackage);
        return "redirect:/user/form";
    }


    @PostMapping("/checkout")
    public String checkout(@ModelAttribute UserRegistrationDto userRegistrationDto, HttpSession session, Model model) {

        // save user registration data in session
        session.setAttribute("userRegistration", userRegistrationDto);

        // create transaction
        InvestmentPackage investmentPackage = cart.getPackage();

        // create a new wallet address for this transaction
        String paymentWalletAddress;
        try {
            String createWalletApiUrl = "http://localhost:3000/api/tron/create-account";
            String apiResponse = tronWebService.makeApiRequest(createWalletApiUrl);
            ObjectMapper mapper = new ObjectMapper();
            WalletResponse response = mapper.readValue(apiResponse, WalletResponse.class);
            paymentWalletAddress = response.getData().getAddress().getBase58();
        } catch (IOException e) {
            // handle exception when calling the API
            e.printStackTrace();
            return "error";
        }

        // save transaction to the database
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setAmount(investmentPackage.getPrice());
        paymentRequest.setWalletAddress(paymentWalletAddress);
        transactionService.createTransaction(paymentRequest);

        // add necessary information to the model
        model.addAttribute("walletAddress", paymentWalletAddress);
        model.addAttribute("amount", investmentPackage.getPrice());
        model.addAttribute("timer", 30 * 60); // 30 minutes

        // clear the cart
        cart.clear();

        // redirect to the payment page
        return "payment";
    }


    @GetMapping("/payment-confirmed")
    public String paymentConfirmed(HttpSession session) {
        // retrieve the user registration data from session
        UserRegistrationDto userRegistrationDto = (UserRegistrationDto) session.getAttribute("userRegistration");
        // create the user
        userService.createUser(userRegistrationDto);
        // remove the user registration data from session
        session.removeAttribute("userRegistration");
        // redirect to a success page
        return "redirect:/success";
    }
}
