package com.healthlink.clinicsystem.controller;

import com.healthlink.clinicsystem.entity.User;
import com.healthlink.clinicsystem.entity.Role;
import com.healthlink.clinicsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers(); // Load all users
        model.addAttribute("users", users);
        model.addAttribute("roles", Role.values());   // Role dropdown
        return "users/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());       // Empty form
        model.addAttribute("roles", Role.values());
        return "users/form";
    }

    @PostMapping
    public String createUser(@ModelAttribute User user) {
        userService.createUser(user);                 // Save new user
        return "redirect:/users?success=created";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found")); // Find user
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        return "users/form";
    }

    @PostMapping("/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute User user) {
        userService.updateUser(id, user);             // Update user
        return "redirect:/users?success=updated";
    }

    @PostMapping("/{id}/deactivate")
    public String deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);               // Mark user as inactive
        return "redirect:/users?success=deactivated";
    }
}
