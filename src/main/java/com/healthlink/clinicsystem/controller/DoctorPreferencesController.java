package com.healthlink.clinicsystem.controller;

import com.healthlink.clinicsystem.entity.User;
import com.healthlink.clinicsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/settings")
public class DoctorPreferencesController {

    @Autowired
    private UserService userService;

    @GetMapping("/doctorpreferences")
    public String getDoctorPreferences(Model model, Principal principal) {
        // Fetch current logged-in user (doctor should have User account)
        User user = userService.findByUsername(principal.getName());

        // Add data to model
        model.addAttribute("user", user);
        model.addAttribute("username", user.getUsername());

        return "settings/doctorpreferences";
    }

    @PostMapping("/doctorpreferences")
    public String saveDoctorPreferences(
            @RequestParam("theme") String theme,
            @RequestParam("fontSize") Integer fontSize,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            // Save new theme and font size - works for both doctors and receptionists
            userService.updatePreferences(
                    principal.getName(),
                    theme,
                    fontSize
            );
            redirectAttributes.addFlashAttribute("success", "Doctor preferences updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update preferences: " + e.getMessage());
        }

        return "redirect:/doctor/dashboard";
    }
}