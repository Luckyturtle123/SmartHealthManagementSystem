package com.mukesh.smarthealthmanagement.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.mukesh.smarthealthmanagement.entities.User;

import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey="Mukesh@123";

    // Generate JWT Token
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername()) // Set the username as the subject
                .setIssuedAt(new Date()) // Set the issued time
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))  // 1 hour expiration time
                .signWith(SignatureAlgorithm.HS256, secretKey)  // Use secretKey for signing
                .compact();
    }

    // Extract username from the JWT token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract claim from the JWT token
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);  // Extract all claims
        return claimsResolver.apply(claims); // Apply the function on the claims
    }

    // Extract all claims from the JWT token
    private Claims extractAllClaims(String token) {
        return Jwts.parser()  
                .setSigningKey(secretKey) // Setting up the signing key
                .parseClaimsJws(token)  // Parsing the token
                .getBody(); // Getting the claims body
    }

    // Checking if the token is expired
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date()); // Check if the token's expiration date is before the current date
    }

    // Extracting expiration date from the JWT token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Validating the JWT token with the UserDetails
    public boolean validateToken(String token, UserDetails userDetails) {
        return (userDetails.getUsername().equals(extractUsername(token)) && !isTokenExpired(token));  // Validate the username and expiration
    }
}
