package com.example.gacapp.config;

import com.example.gacapp.model.User;
import com.example.gacapp.model.UserRole;
import com.example.gacapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * DataInitializer - Initializes default admin account on application startup
 * This component runs once when the application starts and creates a default admin user
 * if it doesn't already exist in the database.
 * 
 * Configuration is read from application.yml under app.default-admin.*
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.default-admin.email}")
    private String adminEmail;

    @Value("${app.default-admin.password}")
    private String adminPassword;

    @Value("${app.default-admin.first-name}")
    private String adminFirstName;

    @Value("${app.default-admin.last-name}")
    private String adminLastName;

    /**
     * Run method called during application startup
     * Creates default admin account if it doesn't exist
     */
    @Override
    @Transactional
    public void run(String... args) {
        try {
            // Check if default admin already exists
            if (!userRepository.existsByEmail(adminEmail)) {
                log.info("Default admin account not found. Creating default admin account...");
                createDefaultAdmin();
            } else {
                log.info("Default admin account already exists with email: {}", adminEmail);
            }
        } catch (Exception e) {
            log.error("Error during data initialization while creating default admin", e);
            throw new RuntimeException("Failed to initialize application data", e);
        }
    }

    /**
     * Creates the default admin user with configured credentials
     */
    private void createDefaultAdmin() {
        try {
            // Create admin user with all required fields
            User adminUser = User.builder()
                    .firstName(adminFirstName)
                    .lastName(adminLastName)
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .role(UserRole.ADMIN)
                    .build();

            // Save admin user to database
            User savedUser = userRepository.save(adminUser);

            log.info("Default admin account created successfully with email: {} and id: {}", 
                    savedUser.getEmail(), savedUser.getId());
            log.info("Admin user: {} {}", savedUser.getFirstName(), savedUser.getLastName());

        } catch (Exception e) {
            log.error("Failed to create default admin account", e);
            throw new RuntimeException("Failed to create default admin account", e);
        }
    }
}
