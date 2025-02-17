package com.example;

import com.example.controller.AuthController;
import com.example.entity.User;
import com.example.repository.UserRepository;
import com.example.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_ShouldReturnErrorMessage_WhenUsernameExists() {
        String username = "existingUser";
        String password = "password";
        Model model = new BindingAwareModelMap();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new User()));

        String viewName = authController.registerUser(username, password, model);

        assertEquals("register", viewName);
        assertEquals("Username already taken. Please choose a different username.", model.getAttribute("errorMessage"));
    }

    @Test
    void registerUser_ShouldReturnLoginMessage_WhenRegistrationSuccessful() {
        String username = "newUser";
        String password = "password";
        Model model = new BindingAwareModelMap();
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

        String viewName = authController.registerUser(username, password, model);

        assertEquals("login", viewName);
        assertEquals("User registered successfully. Please login.", model.getAttribute("message"));
        verify(userRepository, times(1)).save(any(User.class));
    }
}
