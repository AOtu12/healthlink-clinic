package com.healthlink.clinicsystem.api;

import com.healthlink.clinicsystem.dto.AuthenticationRequest;
import com.healthlink.clinicsystem.dto.AuthenticationResponse;
import com.healthlink.clinicsystem.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    @Autowired
    private AuthenticationManager authenticationManager; // Handles login authentication

    @Autowired
    private JwtTokenProvider jwtTokenProvider; // Generates JWT tokens

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody AuthenticationRequest request) {

        // Authenticate username & password
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()
                )
        );

        // Extract user role
        String role = auth.getAuthorities().iterator().next().getAuthority();

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(request.getUsername(), role);

        // Return token + user info
        return new AuthenticationResponse(token, request.getUsername(), role);
    }

    @GetMapping("/profile")
    public String profile() {
        return "JWT API is working!"; // Simple test endpoint
    }
}
