package com.mlmfreya.ferya2.component;

import com.mlmfreya.ferya2.model.User;
import com.mlmfreya.ferya2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;


/*
@ControllerAdvice
public class GlobalControllerAdvice {
    private final UserService userService;

    @Autowired
    public GlobalControllerAdvice(UserService userService) {
        this.userService = userService;
    }



    @ModelAttribute
    public void addAttributes(Model model, Principal principal){
        if (principal != null) {
            User user = userService.findByUsername(principal.getName());
            if (user != null) {
                model.addAttribute("user", user);
            }
        }
    }

}
*/
