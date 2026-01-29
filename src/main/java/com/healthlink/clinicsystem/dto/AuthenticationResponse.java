package com.healthlink.clinicsystem.dto;

public class AuthenticationResponse {

    private String token;     // JWT token
    private String username;  // Authenticated user
    private String role;      // User role

    public AuthenticationResponse(String token, String username, String role) {
        this.token = token;
        this.username = username;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }
}
