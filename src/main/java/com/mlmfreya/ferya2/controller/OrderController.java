package com.mlmfreya.ferya2.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mlmfreya.ferya2.dto.UserRegistrationDto;
import com.mlmfreya.ferya2.dto.WalletResponse;
import com.mlmfreya.ferya2.model.*;
import com.mlmfreya.ferya2.repository.TransactionRepository;
import com.mlmfreya.ferya2.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;


@Controller
@RequestMapping("/shop")
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

    @Autowired
    private UserService userService;
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WalletService walletService;

    @Autowired
    private PaymentWatcherService paymentWatcherService;
    @GetMapping("/list")
    public String listPackages(Model model) {
        if(this.cart != null) {
            this.cart.clear();
        }
        List<InvestmentPackage> packages = packageService.getAllPackages();
        model.addAttribute("packages", packages);

        return "shop/list";
    }
    @PostMapping("/add-to-cart")
    public String addToCart(@RequestParam("packageId") Long packageId,
                            @RequestParam("investmentAmount") BigDecimal investmentAmount,
                            Model model, HttpSession session) {
        InvestmentPackage investmentPackage = packageService.getPackage(packageId);
        if(this.cart == null) {
            this.cart.clear();
        }

        // Validate that the investment amount is not less than the minimum investment amount
        if (investmentAmount.compareTo(investmentPackage.getMinInvestmentAmount()) < 0) {
            model.addAttribute("error", "The minimum investment amount for this package is " + investmentPackage.getMinInvestmentAmount());
            return "user/packages";
        }


        cart.addPackage(investmentPackage);
        // Save the investment amount in the session or cart
        cart.setInvestmentAmount(investmentAmount);

        // Save cart in the session
        session.setAttribute("cart", cart);

        return "redirect:/shop/form";
    }


    @GetMapping("/form")
    public String showForm(HttpSession session, Model model) {
        // Get the cart from the session
        cart = (ShoppingCart) session.getAttribute("cart");
        if (cart == null) {
            return "redirect:/shop/list"; // Or wherever you want to redirect if there is no cart
        }

        // Add cart to the model
        model.addAttribute("cart", cart);
        // Render the form view
        return "user/form";
    }




    @PostMapping("/checkout")
    public String checkout(@ModelAttribute("user") UserRegistrationDto userRegistrationDto, HttpSession session, Model model) {
         cart = (ShoppingCart) session.getAttribute("cart");
        if (cart == null) {
            return "redirect:/shop/list"; // Or wherever you want to redirect if there is no cart
        }
        // Get the parent referral code from the registration form
        String parentReferralCode = userRegistrationDto.getParentReferralCode();
        User parent = null;

        if(parentReferralCode != null && !parentReferralCode.isEmpty()) {
            parent = userService.getUserByReferralCode(parentReferralCode);
            if (parent == null) {
                model.addAttribute("errorMessage", "Parent referral code does not exist.");
                return "user/form";
            } else if(parent.getLeftChild() != null && parent.getRightChild() != null) {
                model.addAttribute("errorMessage", "Parent already has two children. No room for more.");
                return "user/form";
            } else {
                session.setAttribute("parentUser", parent);
            }
        }


        // save user registration data in session
        session.setAttribute("userRegistration", userRegistrationDto);
        // create transaction
        InvestmentPackage investmentPackage = cart.getPackage();
        // create a new wallet address for this transaction
        String paymentWalletAddress;
        try {
            String createWalletApiUrl = "http://localhost:3000/api/tron/createAccount";
            String apiResponse = tronWebService.makeApiRequest(createWalletApiUrl,"POST");
            ObjectMapper mapper = new ObjectMapper();
            WalletResponse response = mapper.readValue(apiResponse, WalletResponse.class);
            Wallet wallet = new Wallet();
            wallet.setHex(response.getData().getAddress().getHex());
            wallet.setBase58(response.getData().getAddress().getBase58());
            wallet.setPrivateKey(response.getData().getPrivateKey());
            wallet.setPublicKey(response.getData().getPublicKey());
            wallet.setEmail(userRegistrationDto.getEmail());

            walletService.saveWallet(wallet);
            paymentWalletAddress = response.getData().getAddress().getBase58();
        } catch (IOException e) {
            // handle exception when calling the API
            e.printStackTrace();
            return "error";
        }

        // save transaction to the database
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setWalletAddress(paymentWalletAddress);
        paymentRequest.setAmount(cart.getInvestmentAmount());
        paymentRequest.setFromAddress(userRegistrationDto.getWalletAddress());
        paymentRequest.setEmail(userRegistrationDto.getEmail());
        paymentRequest.setName(userRegistrationDto.getName());
        paymentRequest.setMobileNumber(userRegistrationDto.getMobileNumber());
        paymentRequest.setPassword(userRegistrationDto.getPassword());
        paymentRequest.setInvestmentPackage(investmentPackage);

        // Set parentId if parent is not null
        if (parent != null) {
            paymentRequest.setParentId(parent.getId());
        }
        transactionService.createTransaction(paymentRequest,investmentPackage);

        // add necessary information to the model
        model.addAttribute("walletAddress", paymentWalletAddress);
        model.addAttribute("timer", 30 * 60); // 30 minutes
        model.addAttribute("amount",paymentRequest.getAmount());
        paymentWatcherService.watchPayment(paymentWalletAddress, cart.getInvestmentAmount());

        // clear the cart
        cart.clear();

        // Save necessary data to session
        session.setAttribute("walletAddress", paymentWalletAddress);
        session.setAttribute("timer", 30 * 60); // 30 minutes
        session.setAttribute("amount", paymentRequest.getAmount());
        // redirect to the payment page
        return "redirect:/shop/payment";
    }

    @GetMapping("/payment")
    public String showPaymentPage(HttpSession session, Model model) {
        String walletAddress = (String) session.getAttribute("walletAddress");
        Integer timer = (Integer) session.getAttribute("timer");
        BigDecimal amount = (BigDecimal) session.getAttribute("amount");

        if (walletAddress == null || timer == null || amount == null) {
            // If any of the required data is missing, redirect back to shop
            return "redirect:/shop/list";
        }

        model.addAttribute("walletAddress", walletAddress);
        model.addAttribute("timer", timer);
        model.addAttribute("amount", amount);

        // If all data is present, display the payment page
        return "payment";
    }

    @GetMapping("/backToShop")
    public String backToShop(HttpSession session) {
        // Reset the session
        session.invalidate();

        // Redirect back to shop
        return "redirect:/shop/list";
    }


    @GetMapping("/payment-confirmed")
    public String paymentConfirmed(HttpSession session) {

        // retrieve the user registration data from session
        UserRegistrationDto userRegistrationDto = (UserRegistrationDto) session.getAttribute("userRegistration");

        // retrieve the cart from session
        ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");

        // remove the cart and user registration data from session
        session.removeAttribute("cart");
        session.removeAttribute("userRegistration");

        // redirect to a success page
        return "redirect:/success";
    }








}
