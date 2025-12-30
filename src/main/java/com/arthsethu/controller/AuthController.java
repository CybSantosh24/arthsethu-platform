package com.arthsethu.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.arthsethu.model.Role;
import com.arthsethu.model.User;
import com.arthsethu.repository.UserRepository;

@Controller
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String email, 
                              @RequestParam String password,
                              @RequestParam(required = false) String adminCode,
                              Model model) {
        try {
            // Check if user already exists
            if (userRepository.existsByEmail(email)) {
                model.addAttribute("error", "User with this email already exists");
                return "register";
            }

            // Create new user
            User user = new User();
            user.setEmail(email);
            user.setPasswordHash(passwordEncoder.encode(password));
            
            // Check if admin code is provided (simple admin registration)
            if ("ARTHSETHU_ADMIN_2024".equals(adminCode)) {
                user.setRole(Role.ADMIN);
                logger.info("Admin user registered: {}", email);
            } else {
                user.setRole(Role.USER);
                logger.info("Regular user registered: {}", email);
            }

            userRepository.save(user);

            model.addAttribute("success", "Registration successful! Please login.");
            return "login";

        } catch (Exception e) {
            logger.error("Error during registration: {}", e.getMessage());
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "register";
        }
    }
}