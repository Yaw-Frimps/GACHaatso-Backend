package com.example.gacapp.config;

import com.example.gacapp.exception.DataInitialisationException;
import com.example.gacapp.model.ApprovalStatus;
import com.example.gacapp.model.User;
import com.example.gacapp.model.UserRole;
import com.example.gacapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GacAppProperties gacAppProperties;

    @Override
    @Transactional
    public void run(String... args) {

        String adminEmail = gacAppProperties.getDefaultAdmin().getEmail();

        if (!userRepository.existsByEmail(adminEmail)) {
            log.info("Default admin account not found. Creating default admin account...");
            createDefaultAdmin();
        } else {
            log.info("Default admin account already exists with email: {}", adminEmail);
        }
    }

    private void createDefaultAdmin() {

        try {

            GacAppProperties.DefaultAdmin admin =
                    gacAppProperties.getDefaultAdmin();

            User adminUser = User.builder()
                    .firstName(admin.getFirstName())
                    .lastName(admin.getLastName())
                    .email(admin.getEmail())
                    .password(passwordEncoder.encode(admin.getPassword()))
                    .role(UserRole.ADMIN)
                    .enabled(true)
                    .approvalStatus(ApprovalStatus.APPROVED)
                    .build();

            User savedUser = userRepository.save(adminUser);

            log.info(
                    "Default admin account created successfully with email: {} and id: {}",
                    savedUser.getEmail(),
                    savedUser.getId()
            );

        } catch (Exception e) {

            throw new DataInitialisationException(
                    "Failed to create default admin account",
                    e
            );
        }
    }
}