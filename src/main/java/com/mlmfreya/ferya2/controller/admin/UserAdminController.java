package com.mlmfreya.ferya2.controller.admin;

import com.mlmfreya.ferya2.model.User;
import com.mlmfreya.ferya2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("admin/user")
public class UserAdminController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/list")
    public String userList(Model model){
        List<User> users = userRepository.findAll();
        model.addAttribute("users",users);
        return "admin/user/list";
    }

    @GetMapping("/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        model.addAttribute("user", user);
        return "admin/user/edit";  // return the name of your edit form template
    }

    @PostMapping("/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute User updatedUser, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "editUser";  // return back to the form if there are errors
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));

        user.setEmail(updatedUser.getEmail());
        user.setFullName(updatedUser.getFullName());
        if (!updatedUser.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(updatedUser.getPassword());
            user.setPassword(encodedPassword);
        }
        user.setRole(updatedUser.getRole());
        user.setTronWalletAddress(updatedUser.getTronWalletAddress());
        user.setReferralCode(updatedUser.getReferralCode());
        user.setResetPasswordToken(updatedUser.getResetPasswordToken());
        user.setEmailVerificationToken(updatedUser.getEmailVerificationToken());
        user.setEmailVerified(updatedUser.isEmailVerified());
        user.setBalance(updatedUser.getBalance());

        userRepository.save(user);

        return "redirect:/admin/user";  // redirect to the list of users after successful update
    }




}
