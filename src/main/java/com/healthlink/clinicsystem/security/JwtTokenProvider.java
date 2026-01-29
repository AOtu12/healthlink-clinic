package com.healthlink.clinicsystem.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final String SECRET = "very-secret-healthlink-key-123456789012345"; // JWT signing key
    private final long EXPIRATION = 24 * 60 * 60 * 1000; // 24 hours

    private Key getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes()); // Generate HMAC key
    }

    public String generateToken(String username, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + EXPIRATION);

        // Build JWT token
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            // Parse + validate signature + expiration
            Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            return false; // Invalid token
        }
    }

    public String getUsernameFromToken(String token) {
        // Extract "sub" field
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
