package com.healthlink.clinicsystem.controller;

import com.healthlink.clinicsystem.entity.UserPreferences;
import com.healthlink.clinicsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/settings")
public class SettingsController {

    @Autowired
    private UserService userService;

    // ✅ Show preferences page
    @GetMapping("/preferences")
    public String showPreferences(Authentication auth,
                                  @RequestParam(value = "success", required = false) String success,
                                  @RequestParam(value = "error", required = false) String error,
                                  Model model) {
        String username = auth.getName();
        UserPreferences prefs = userService.getPreferencesFor(username);
        model.addAttribute("username", username);
        model.addAttribute("preferences", prefs);
        model.addAttribute("success", success);
        model.addAttribute("error", error);
        model.addAttribute("user", userService.findByUsername(username));
        return "settings/preferences";
    }

    // ✅ Save preferences
    @PostMapping("/preferences")
    public String updatePreferences(Authentication auth,
                                    @ModelAttribute("preferences") UserPreferences p) {
        String username = auth.getName();
        userService.updatePreferences(
                username,
                p.getTheme(),
                p.getFontSize()

        );
        return "redirect:/settings/preferences?success=Settings saved successfully!";
    }

    // ✅ Update profile email
    @PostMapping("/profile")
    public String updateEmail(Authentication auth, @RequestParam String email) {
        userService.updateEmail(auth.getName(), email);
        return "redirect:/settings/preferences?success=Email updated successfully!";
    }

    // ✅ Change password
    @PostMapping("/password")
    public String updatePassword(Authentication auth,
                                 @RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Model model) {
        if (!newPassword.equals(confirmPassword)) {
            return "redirect:/settings/preferences?error=Passwords do not match";
        }

        try {
            userService.changePassword(auth.getName(), currentPassword, newPassword);
            return "redirect:/settings/preferences?success=Password changed successfully!";
        } catch (RuntimeException e) {
            return "redirect:/settings/preferences?error=" + e.getMessage();
        }
    }
}
