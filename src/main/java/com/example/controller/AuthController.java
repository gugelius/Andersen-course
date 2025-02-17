package com.example.controller;

import com.example.entity.User;
import com.example.repository.UserRepository;
import com.example.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String password, Model model) {
        System.out.println("Start registration: " + username);

        // Проверка на существование пользователя с таким же именем
        Optional<User> existingUser = userRepository.findByUsername(username);
        if (existingUser.isPresent()) {
            model.addAttribute("errorMessage", "Username already taken. Please choose a different username.");
            return "register"; // Возврат на страницу регистрации с сообщением об ошибке
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("ROLE_USER"); // Установите роль "ROLE_USER" по умолчанию
        userRepository.save(user);
        System.out.println("User registered successfully: " + username);
        model.addAttribute("message", "User registered successfully. Please login.");
        return "login";
    }

    @PostMapping("/authenticate")
    public String authenticate(@RequestParam String username, @RequestParam String password, Model model) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            String role = userDetails.getAuthorities().stream()
                    .map(authority -> authority.getAuthority())
                    .findFirst().orElse("ROLE_USER");

            System.out.println("User authenticated: " + username + ", Role: " + role);

            model.addAttribute("role", role); // Добавляем информацию о роли в модель

            if (role.equals("ROLE_ADMIN")) {
                return "redirect:/admin";
            } else {
                return "redirect:/user/menu";
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Invalid username or password");
            return "login";
        }
    }
}
