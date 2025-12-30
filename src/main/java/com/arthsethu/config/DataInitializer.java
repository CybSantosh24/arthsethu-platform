package com.arthsethu.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.arthsethu.model.Role;
import com.arthsethu.model.SubscriptionTier;
import com.arthsethu.model.User;
import com.arthsethu.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initializeAdminUser();
    }

    private void initializeAdminUser() {
        String adminEmail = "admin@arthsethu.com";
        
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setPasswordHash(passwordEncoder.encode("admin123")); // Default password
            admin.setRole(Role.ADMIN);
            admin.setTier(SubscriptionTier.SHIKHAR); // Give admin highest tier
            
            userRepository.save(admin);
            
            logger.info("=".repeat(60));
            logger.info("ADMIN USER CREATED SUCCESSFULLY!");
            logger.info("Email: {}", adminEmail);
            logger.info("Password: admin123");
            logger.info("Role: ADMIN");
            logger.info("Please change the password after first login!");
            logger.info("=".repeat(60));
        } else {
            logger.info("Admin user already exists: {}", adminEmail);
        }
    }
}