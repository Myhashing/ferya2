package com.mlmfreya.ferya2.controller.admin;

import com.mlmfreya.ferya2.model.User;
import com.mlmfreya.ferya2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @PostMapping("/admin/user/{id}")
    @ResponseBody
    public User updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setEmail(updatedUser.getEmail());
                    user.setFullName(updatedUser.getFullName());
                    user.setPassword(updatedUser.getPassword());
                    user.setRole(updatedUser.getRole());
                    user.setTronWalletAddress(updatedUser.getTronWalletAddress());
                    user.setReferralCode(updatedUser.getReferralCode());
                    user.setResetPasswordToken(updatedUser.getResetPasswordToken());
                    user.setEmailVerificationToken(updatedUser.getEmailVerificationToken());
                    user.setEmailVerified(updatedUser.isEmailVerified());
                    user.setBalance(updatedUser.getBalance());
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
    }

}
