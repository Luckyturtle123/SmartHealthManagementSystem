package com.mukesh.smarthealthmanagement.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import com.mukesh.smarthealthmanagement.entities.LoginRequest;
import com.mukesh.smarthealthmanagement.entities.User;
import com.mukesh.smarthealthmanagement.services.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    //Endpoint to register as a user
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        System.out.println("Registering user: " + user.getUsername()); // Log for debugging
        return ResponseEntity.ok(userService.registerUser(user));
    }

    //Endpoint to login by giving username and password
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        String token = userService.authenticateUser(loginRequest.getUsernameOrEmail(), loginRequest.getPassword());

        if (token != null) {
            return ResponseEntity.ok("Login successful! Token: " + token); // Return token on success
        } else {
            return ResponseEntity.status(401).body("Invalid username or password"); // Error response for invalid credentials
        }
    }
    //Endpoint to logout
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return ResponseEntity.ok("Successfully logged out");
    }

}
