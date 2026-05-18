package com.example.gacapp.service.impl;

import com.example.gacapp.dto.request.LoginRequest;
import com.example.gacapp.dto.request.RegisterRequest;
import com.example.gacapp.dto.response.LoginResponse;
import com.example.gacapp.dto.response.RegisterResponse;
import com.example.gacapp.exception.*;
import com.example.gacapp.model.ApprovalStatus;
import com.example.gacapp.model.User;
import com.example.gacapp.model.UserRole;
import com.example.gacapp.repository.UserRepository;
import com.example.gacapp.security.JwtService;
import com.example.gacapp.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // ================= REGISTER =================

    @Override
    @Transactional
    @CacheEvict(value = "users", allEntries = true) // Clear user cache on registration
    public RegisterResponse registerUser(RegisterRequest request) {
        log.info("Attempting to register user with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed: Email {} already in use", request.getEmail());
            throw new EmailAlreadyExistException("Email already registered");
        }

        if (request.getRole() == null) {
            throw new IllegalArgumentException("Role is required");
        }

        try {
            ApprovalStatus approvalStatus;
            boolean enabled;

            // ✅ Role-based approval logic
            if (request.getRole() == UserRole.LEADER) {
                approvalStatus = ApprovalStatus.PENDING;
                enabled = false;
            } else {
                approvalStatus = ApprovalStatus.NOT_REQUIRED;
                enabled = true;
            }

            var user = User.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(request.getRole())
                    .approvalStatus(approvalStatus)
                    .enabled(enabled)
                    .build();

            var savedUser = userRepository.save(user);

            log.info("User successfully registered with id: {}", savedUser.getId());

            return RegisterResponse.builder()
                    .id(savedUser.getId())
                    .firstName(savedUser.getFirstName())
                    .lastName(savedUser.getLastName())
                    .email(savedUser.getEmail())
                    .role(savedUser.getRole().name())
                    .createdAt(savedUser.getCreatedAt() != null ? savedUser.getCreatedAt().toString() : null)
                    .updatedAt(savedUser.getUpdatedAt() != null ? savedUser.getUpdatedAt().toString() : null)
                    .build();

        } catch (Exception e) {
            log.error("Error occurred while registering user with email: {}", request.getEmail(), e);
            throw new RegistrationFailedException("Registration failed, please try again later");
        }
    }

    // ================= LOGIN =================

    @Override
    @Transactional
    public LoginResponse loginUser(LoginRequest request) {
        log.info("Attempting to login user with email: {}", request.getEmail());

        try {
            var user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> {
                        log.warn("Login failed: User not found with email: {}", request.getEmail());
                        return new UserNotFoundException("Invalid email or password");
                    });

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                log.warn("Login failed: Invalid password for user: {}", request.getEmail());
                throw new InvalidCredentialsException("Invalid email or password");
            }

            // ================= 🚨 APPROVAL CHECK =================

            if (!isUserAllowedToLogin(user)) {
                log.warn("Login blocked: User {} is not approved", user.getEmail());
                throw new ApprovalRequiredException("Your account is pending admin approval");
            }

            // =====================================================

            var jwtToken = jwtService.generateToken(user);

            log.info("User successfully logged in: {}", request.getEmail());

            return LoginResponse.builder()
                    .id(user.getId())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .token(jwtToken)
                    .role(user.getRole().name())
                    .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null)
                    .updatedAt(user.getUpdatedAt() != null ? user.getUpdatedAt().toString() : null)
                    .build();

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error occurred during login for user: {}", request.getEmail(), e);
            throw new LoginFailedException("Login failed, please try again later");
        }
    }

    // ================= HELPER =================

    private boolean isUserAllowedToLogin(User user) {

        // Defensive null handling
        ApprovalStatus status = user.getApprovalStatus();

        // ADMIN & MEMBER → always allowed
        if (user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.MEMBER) {
            return true;
        }

        // LEADER → must be approved
        if (user.getRole() == UserRole.LEADER) {
            return status == ApprovalStatus.APPROVED;
        }

        return false;
    }
}