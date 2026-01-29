package com.healthlink.clinicsystem.web;

import com.healthlink.clinicsystem.entity.UserPreferences;
import com.healthlink.clinicsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalViewAdvice {

    @Autowired
    private UserService userService;

    // Inject user preferences into all views
    @ModelAttribute("preferences")
    public UserPreferences globalPreferences(Authentication auth) {
        if (auth == null) return new UserPreferences(); // Default if no login
        try {
            return userService.getPreferencesFor(auth.getName());
        } catch (Exception e) {
            return new UserPreferences(); // Fallback
        }
    }

    // Inject logged-in username
    @ModelAttribute("username")
    public String globalUsername(Authentication auth) {
        return (auth != null) ? auth.getName() : "Guest";
    }
}
