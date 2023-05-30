package com.mlmfreya.ferya2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/blog/")
public class BlogController {

    @GetMapping("crypto-vs-fiat")
    public String CryptovsFiat(){
        return "blog/crypto-vs-fiat";
    }
    @GetMapping("navigating-the-tension-between")
    public String navigatingthetensionbetween(){
        return "blog/navigating-the-tension-between";
    }
    @GetMapping("from-early-adopters")
    public String adopters(){
        return "blog/from-early-adopters";
    }

    @GetMapping("deconstructing-the-relationship")
    public String relationship(){
        return "blog/deconstructing-the-relationship";
    }
    @GetMapping("an-overview-of-8-types-of-tokens-in-web3")
    public String web3(){
        return "blog/an-overview-of-8-types-of-tokens-in-web3";
    }
    @GetMapping("defi-kyc-the-contradiction-of-knowing-your-customer-in-web3")
    public String customerinweb3(){
        return "blog/defi-kyc-the-contradiction-of-knowing-your-customer-in-web3";
    }

    @GetMapping("Revolutionize-Your-Passive-Income")
    public String Revolutionize(){
        return "blog/Revolutionize-Your-Passive-Income";
    }
}
