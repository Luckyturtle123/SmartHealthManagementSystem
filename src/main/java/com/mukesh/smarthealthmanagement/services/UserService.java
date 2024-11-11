package com.mukesh.smarthealthmanagement.services;

import com.mukesh.smarthealthmanagement.entities.User;
import com.mukesh.smarthealthmanagement.Role; // Make sure to use the correct enum import
import com.mukesh.smarthealthmanagement.config.JwtUtil;
import com.mukesh.smarthealthmanagement.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class); // Using SLF4J for logging

    public User registerUser(User user) {
        // Encrypt the password before saving
        logger.info("Registering user: {}", user.getUsername());

        // Check if the user already exists by username or email
        if (userRepository.findByUsernameOrEmail(user.getUsername(), user.getEmail()) != null) {
            logger.error("User with username or email already exists");
            throw new IllegalArgumentException("User with the given username or email already exists.");
        }

        // Encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Assign default role if no roles are provided
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Set<Role> defaultRole = new HashSet<>();
            defaultRole.add(Role.USER); // Default to "USER" if no role is provided
            user.setRoles(defaultRole);
        }

        // Save the user to the database
        return userRepository.save(user);
    }

    public String authenticateUser(String usernameOrEmail, String password) {
        logger.info("Authenticating user: {}", usernameOrEmail);

        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        if (user == null) {
            logger.error("User not found: {}", usernameOrEmail);
            return null; 
        }

        // Check if the password matches
        if (passwordEncoder.matches(password, user.getPassword())) {
            logger.info("User authenticated successfully: {}", usernameOrEmail);
            return jwtUtil.generateToken(user); // Generate JWT token
        } else {
            logger.error("Invalid password for user: {}", usernameOrEmail);
            return null; 
        }
    }
}
