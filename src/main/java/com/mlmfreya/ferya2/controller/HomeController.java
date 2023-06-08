package com.mlmfreya.ferya2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String showHomePage() {
        return "home";
    }


    @GetMapping("/public/about")
    public String showAboutPage() {
        return "about";
    }

    @GetMapping("/public/newsletter")
    public String showNewsletter() {
        return "newsletter";
    }

    @GetMapping("/public/signin")
    public String showSignin() {
        return "signin2";
    }
    @GetMapping("/public/terms")
    public String terms() {
        return "terms";
    }
    @GetMapping("/public/privacy")
    public String privacy() {
        return "privacy";
    }

}