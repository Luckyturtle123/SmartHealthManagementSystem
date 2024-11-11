package com.mukesh.smarthealthmanagementtest.services;

import com.mukesh.smarthealthmanagement.Role;
import com.mukesh.smarthealthmanagement.config.JwtUtil;
import com.mukesh.smarthealthmanagement.entities.User;
import com.mukesh.smarthealthmanagement.repositories.UserRepository;
import com.mukesh.smarthealthmanagement.services.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.HashSet;
//import java.util.Optional;
//import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testUser");
        user.setEmail("testUser@example.com");
        user.setPassword("password123");
    }

    @Test
    void registerUser_ShouldRegisterSuccessfully_WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findByUsernameOrEmail(user.getUsername(), user.getEmail())).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User registeredUser = userService.registerUser(user);

        // Assert
        assertNotNull(registeredUser);
        assertEquals("encodedPassword", registeredUser.getPassword());
        assertEquals(1, registeredUser.getRoles().size());
        assertTrue(registeredUser.getRoles().contains(Role.USER));

        verify(userRepository).findByUsernameOrEmail(user.getUsername(), user.getEmail());
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("password123");
    }

    @Test
    void registerUser_ShouldThrowException_WhenUserAlreadyExists() {
        // Arrange
        when(userRepository.findByUsernameOrEmail(user.getUsername(), user.getEmail())).thenReturn(user);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.registerUser(user));
        assertEquals("User with the given username or email already exists.", exception.getMessage());

        verify(userRepository).findByUsernameOrEmail(user.getUsername(), user.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_ShouldAssignDefaultRole_WhenRolesNotProvided() {
        // Arrange
        user.setRoles(null); // No roles provided
        when(userRepository.findByUsernameOrEmail(user.getUsername(), user.getEmail())).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User registeredUser = userService.registerUser(user);

        // Assert
        assertNotNull(registeredUser);
        assertEquals(1, registeredUser.getRoles().size());
        assertTrue(registeredUser.getRoles().contains(Role.USER));

        verify(userRepository).findByUsernameOrEmail(user.getUsername(), user.getEmail());
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("password123");
    }

    @Test
    void authenticateUser_ShouldReturnToken_WhenCredentialsAreValid() {
        // Arrange
        when(userRepository.findByUsernameOrEmail(user.getUsername(), user.getUsername())).thenReturn(user);
        when(passwordEncoder.matches("password123", user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(user)).thenReturn("mockJwtToken");

        // Act
        String token = userService.authenticateUser(user.getUsername(), "password123");

        // Assert
        assertNotNull(token);
        assertEquals("mockJwtToken", token);

        verify(userRepository).findByUsernameOrEmail(user.getUsername(), user.getUsername());
        verify(passwordEncoder).matches("password123", user.getPassword());
        verify(jwtUtil).generateToken(user);
    }

    @Test
    void authenticateUser_ShouldReturnNull_WhenUserNotFound() {
        // Arrange
        when(userRepository.findByUsernameOrEmail(user.getUsername(), user.getUsername())).thenReturn(null);

        // Act
        String token = userService.authenticateUser(user.getUsername(), "password123");

        // Assert
        assertNull(token);

        verify(userRepository).findByUsernameOrEmail(user.getUsername(), user.getUsername());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(any(User.class));
    }

    @Test
    void authenticateUser_ShouldReturnNull_WhenPasswordIsInvalid() {
        // Arrange
        when(userRepository.findByUsernameOrEmail(user.getUsername(), user.getUsername())).thenReturn(user);
        when(passwordEncoder.matches("password123", user.getPassword())).thenReturn(false);

        // Act
        String token = userService.authenticateUser(user.getUsername(), "password123");

        // Assert
        assertNull(token);

        verify(userRepository).findByUsernameOrEmail(user.getUsername(), user.getUsername());
        verify(passwordEncoder).matches("password123", user.getPassword());
        verify(jwtUtil, never()).generateToken(any(User.class));
    }
}
