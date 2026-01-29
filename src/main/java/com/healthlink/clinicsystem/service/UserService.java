package com.healthlink.clinicsystem.service;

import com.healthlink.clinicsystem.entity.User;
import com.healthlink.clinicsystem.entity.UserPreferences;
import com.healthlink.clinicsystem.repository.UserRepository;
import com.healthlink.clinicsystem.repository.UserPreferencesRepository;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPreferencesRepository preferencesRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // --------------------------- USER MANAGEMENT ---------------------------

    public List<User> getAllUsers() {
        return userRepository.findAll(); // All users
    }

    public long countUsers() {
        return userRepository.count();
    }

    public long countPatients() {
        return userRepository.countPatients();
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id); // Old controllers
    }

    @Transactional
    public User createUser(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword())); // Encode password
        }
        user.setIsActive(true);
        return userRepository.save(user);
    }

    @Transactional
    public void updateUser(Long id, User updatedUser) {
        User existing = getById(id);

        existing.setUsername(updatedUser.getUsername());
        existing.setEmail(updatedUser.getEmail());
        existing.setRole(updatedUser.getRole());
        existing.setIsActive(updatedUser.getIsActive());

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        userRepository.save(existing);
    }

    @Transactional
    public void toggleActive(Long id) {
        User user = getById(id);
        user.setIsActive(!user.getIsActive()); // Flip status
        userRepository.save(user);
    }

    @Transactional
    public void deactivateUser(Long id) {
        User user = getById(id);
        user.setIsActive(false); // Disable user
        userRepository.save(user);
    }

    // ----------------------------- EMAIL UPDATE -----------------------------

    @Transactional
    public void updateEmail(String username, String newEmail) {
        User user = findByUsername(username);
        user.setEmail(newEmail);
        userRepository.save(user);
    }

    // ---------------------------- PASSWORD UPDATE ---------------------------

    @Transactional
    public void changePassword(String username, String currentPassword, String newPassword) {

        User user = findByUsername(username);

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Incorrect current password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // ----------------------------- PREFERENCES ------------------------------

    @Transactional
    public void updatePreferences(String username, String theme, Integer fontSize) {

        User user = findByUsername(username);

        UserPreferences prefs = user.getPreferences();

        if (prefs == null) {
            prefs = new UserPreferences();
            user.setPreferences(prefs); // Create if missing
        }

        prefs.setTheme(theme != null ? theme : "light");
        prefs.setFontSize(fontSize != null ? fontSize : 16);

        preferencesRepository.save(prefs);
        userRepository.save(user);
    }

    public UserPreferences getPreferencesFor(String username) {
        User user = findByUsername(username);

        UserPreferences prefs = user.getPreferences();

        if (prefs == null) {
            prefs = new UserPreferences("light", 16); // Default prefs
            user.setPreferences(prefs);
            preferencesRepository.save(prefs);
            userRepository.save(user);
        }

        return prefs;
    }

    // ----------------------------- LOOKUPS ---------------------------------

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null); // Nullable for doctors
    }

    // -------------------------- DEBUG HASH OUTPUT ---------------------------

    @PostConstruct
    public void printSampleHashes() {
        System.out.println("--------------------------------------------------");
        System.out.println("🔐 Sample Password Hashes:");
        System.out.println("Admin: " + passwordEncoder.encode("password"));
        System.out.println("Doctor: " + passwordEncoder.encode("password"));
        System.out.println("Receptionist: " + passwordEncoder.encode("password"));
        System.out.println("--------------------------------------------------");
    }

    @Transactional
    public User createUserWithRole(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        user.setIsActive(true);
        return userRepository.save(user);
    }

    public boolean sendPasswordResetLink(String email) {
        return userRepository.findByEmail(email).isPresent(); // Simple check
    }

}
