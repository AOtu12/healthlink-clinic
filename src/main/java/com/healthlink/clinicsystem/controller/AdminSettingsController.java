package com.healthlink.clinicsystem.controller;

import com.healthlink.clinicsystem.entity.User;
import com.healthlink.clinicsystem.entity.UserPreferences;
import com.healthlink.clinicsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/settings")
public class AdminSettingsController {

    @Autowired
    private UserService userService;


    // --------------------------------------------------------
    //  SETTINGS PAGE
    // --------------------------------------------------------
    @GetMapping
    public String settings(Model model, Authentication auth) {

        String username = auth.getName();

        // Load user
        User user = userService.findByUsername(username);

        // Load or auto-create preferences
        UserPreferences prefs = userService.getPreferencesFor(username);

        model.addAttribute("preferences", prefs);
        model.addAttribute("user", user);
        model.addAttribute("username", username);

        return "admin/settings";
    }


    // --------------------------------------------------------
    //  UPDATE APPEARANCE (Theme + Font Size)
    // --------------------------------------------------------
    @PostMapping
    public String updateAppearance(@ModelAttribute("preferences") UserPreferences prefs,
                                   Authentication auth,
                                   Model model) {

        String username = auth.getName();

        userService.updatePreferences(username, prefs.getTheme(), prefs.getFontSize());

        model.addAttribute("success", "Appearance settings updated successfully!");

        return "redirect:/admin/settings";
    }


    // --------------------------------------------------------
    //  UPDATE PROFILE (Email)
    // --------------------------------------------------------
    @PostMapping("/profile")
    public String updateProfile(Authentication auth,
                                @RequestParam("email") String email,
                                Model model) {

        String username = auth.getName();

        userService.updateEmail(username, email);

        return "redirect:/admin/settings?success=profile-updated";
    }


    // --------------------------------------------------------
    //  CHANGE PASSWORD
    // --------------------------------------------------------
    @PostMapping("/password")
    public String changePassword(Authentication auth,
                                 @RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Model model) {

        String username = auth.getName();

        if (!newPassword.equals(confirmPassword)) {
            return "redirect:/admin/settings?error=password-mismatch";
        }

        try {
            userService.changePassword(username, currentPassword, newPassword);
            return "redirect:/admin/settings?success=password-updated";

        } catch (Exception e) {
            return "redirect:/admin/settings?error=" + e.getMessage();
        }
    }
}
