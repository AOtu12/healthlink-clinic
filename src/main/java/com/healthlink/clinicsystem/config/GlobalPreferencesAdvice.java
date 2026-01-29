package com.healthlink.clinicsystem.config;

import com.healthlink.clinicsystem.entity.UserPreferences;
import com.healthlink.clinicsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalPreferencesAdvice {

    @Autowired
    private UserService userService;

    // This runs automatically before every controller method and injects preferences into all Thymeleaf templates
    @ModelAttribute("preferences")
    public UserPreferences addPreferencesToModel() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
                UserPreferences prefs = userService.getPreferencesFor(auth.getName());
                // ✅ Ensure preferences are never null and have proper defaults
                if (prefs != null) {
                    // Ensure theme is never null
                    if (prefs.getTheme() == null) {
                        prefs.setTheme("light");
                    }
                    // Ensure fontSize is never null
                    if (prefs.getFontSize() == null) {
                        prefs.setFontSize(16);
                    }
                    return prefs;
                }
            }
        } catch (Exception e) {
            // Log the error for debugging
            System.out.println("Error loading preferences for user: " +
                    (SecurityContextHolder.getContext().getAuthentication() != null ?
                            SecurityContextHolder.getContext().getAuthentication().getName() : "unknown") +
                    " - " + e.getMessage());
        }
        // ✅ Consistent fallback defaults
        return new UserPreferences("light", 16);
    }
}