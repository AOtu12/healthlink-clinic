package com.healthlink.clinicsystem.controller;

import com.healthlink.clinicsystem.entity.User;
import com.healthlink.clinicsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ForgotPasswordController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(
            @RequestParam String username,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Model model
    ) {

        // Check if passwords match
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match!");
            return "auth/forgot-password";
        }

        // FIX: handle Optional<User>
        User user = userRepository.findByUsername(username)
                .orElse(null);

        if (user == null) {
            model.addAttribute("error", "Username not found.");
            return "auth/forgot-password";
        }

        // Encrypt and update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        model.addAttribute("success", "Password reset successfully! Please log in.");
        return "auth/forgot-password";
    }
}
