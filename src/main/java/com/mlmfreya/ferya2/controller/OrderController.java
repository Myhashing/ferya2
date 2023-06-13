package com.mlmfreya.ferya2.controller;


import com.binance.connector.client.impl.spot.Pay;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mlmfreya.ferya2.dto.InvestmentTopup;
import com.mlmfreya.ferya2.dto.UserRegistrationDto;
import com.mlmfreya.ferya2.dto.WalletResponse;
import com.mlmfreya.ferya2.model.*;
import com.mlmfreya.ferya2.repository.PaymentRequestUserRepository;
import com.mlmfreya.ferya2.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;

import java.security.Principal;
import java.util.List;


@Controller
@RequestMapping("/shop")
public class OrderController {

    @Value("${tronApiAdress}")
    private String TronApiAddress;

    @Autowired
    private ShoppingCart cart;

    @Autowired
    private InvestmentPackageService packageService;


    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TronWebService tronWebService;

    @Autowired
    private UserService userService;


    @Autowired
    private WalletService walletService;

    @Autowired
    private PaymentWatcherService paymentWatcherService;
    @Autowired
    private InterPaymentWatcherService interPaymentWatcherService;
    @Autowired
    private PaymentRequestUserRepository paymentRequestUserRepository;

    @GetMapping("/package/{id}")
    @ResponseBody
    public InvestmentPackage getPackage(@PathVariable Long id){
        InvestmentPackage investmentPackage = packageService.getPackage(id);
        return investmentPackage;
    }

    @GetMapping("/list")
    public String listPackages(Model model) {
        if(this.cart != null) {
            this.cart.clear();
        }
        List<InvestmentPackage> packages = packageService.getAllPackages();
        model.addAttribute("packages", packages);

        return "shop/list";
    }

    @GetMapping("/reg")
    public String test(){
        return "shop/reg";
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
            return "shop/list";
        }


        cart.addPackage(investmentPackage);
        // Save the investment amount in the session or cart
        cart.setInvestmentAmount(investmentAmount);

        // Save cart in the session
        session.setAttribute("cart", cart);

        return "redirect:/shop/reg";
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
        return "shop/reg";
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
                return "shop/reg";
            } else if(parent.getLeftChild() != null && parent.getRightChild() != null) {
                model.addAttribute("errorMessage", "Parent already has two children. No room for more.");
                return "shop/reg";
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
            String createWalletApiUrl =  TronApiAddress+"/createAccount";
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
        return "/shop/payment";
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



    @GetMapping("/topup")
    public String topup(Model model, Principal principal){
        if (principal != null) {
            User user = userService.findByUsername(principal.getName());
            if (user != null) {
                model.addAttribute("user", user);

            }
        }
        return "shop/topup";
    }

    @PostMapping("/topup")
    public String topupSave(@ModelAttribute PaymentRequestUser paymentRequestUser,
                            Principal principal,
                            Model model){
        if (principal != null) {
            User user = userService.findByUsername(principal.getName());
            if (user != null) {
                model.addAttribute("user", user);
                String paymentWalletAddress;
                Wallet wallet = new Wallet();

                try {
                    String createWalletApiUrl =  TronApiAddress+"/createAccount";
                    String apiResponse = tronWebService.makeApiRequest(createWalletApiUrl,"POST");
                    ObjectMapper mapper = new ObjectMapper();
                    WalletResponse response = mapper.readValue(apiResponse, WalletResponse.class);
                    wallet.setHex(response.getData().getAddress().getHex());
                    wallet.setBase58(response.getData().getAddress().getBase58());
                    wallet.setPrivateKey(response.getData().getPrivateKey());
                    wallet.setPublicKey(response.getData().getPublicKey());
                    wallet.setEmail(user.getEmail());

                    wallet= walletService.saveWallet(wallet);
                    paymentWalletAddress = response.getData().getAddress().getBase58();
                } catch (IOException e) {
                    // handle exception when calling the API
                    e.printStackTrace();
                    return "error";
                }
                paymentRequestUser.setUser(user);
                paymentRequestUser.setStatus(PaymentRequestUser.Status.Pending);
                paymentRequestUser.setWallet(wallet);
                paymentRequestUserRepository.save(paymentRequestUser);
                model.addAttribute("walletAddress",paymentRequestUser.getWallet().getBase58());
                model.addAttribute("timer",30 * 60);
                model.addAttribute("amount",paymentRequestUser.getAmount());
                interPaymentWatcherService.watcher(paymentRequestUser);
                return "/shop/payment";
            }
        }

        return "redirect:/dashboard";
    }




}
