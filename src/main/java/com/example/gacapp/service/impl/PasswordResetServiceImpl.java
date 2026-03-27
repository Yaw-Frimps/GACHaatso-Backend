package com.example.gacapp.service.impl;

import com.example.gacapp.exception.FailedToSendPasswordResetEmailException;
import com.example.gacapp.exception.TokenExpiredException;
import com.example.gacapp.exception.UserNotFoundException;
import com.example.gacapp.model.PasswordResetToken;
import com.example.gacapp.model.User;
import com.example.gacapp.repository.PasswordResetTokenRepository;
import com.example.gacapp.repository.UserRepository;
import com.example.gacapp.service.PasswordResetService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private static final long EXPIRATION_TIME = 15; // 15 minutes

    @Override
    public void requestPasswordReset(String email) {
        if (!userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User not found with email: " + email);
        }

        try{
            String rawToken = UUID.randomUUID().toString();
            String tokenHash = passwordEncoder.encode(rawToken);

            PasswordResetToken token = tokenRepository.findByEmail(email)
                    .orElse(new PasswordResetToken());
            token.setEmail(email);
            token.setTokenHash(tokenHash);
            token.setExpiryDate(LocalDateTime.now().plusMinutes(EXPIRATION_TIME));
            tokenRepository.save(token);

            String resetLink = "http://localhost:8081/reset-password?token=" + rawToken;

            emailService.sendPasswordResetEmail(email, resetLink);
        }catch (MessagingException e){
            throw new FailedToSendPasswordResetEmailException("Failed to send password reset email ", e);
        }


    }

    @Override
    public void resetPassword(String rawToken, String newPassword) {

        PasswordResetToken token = tokenRepository.findAll()
                .stream()
                .filter(t -> passwordEncoder.matches(rawToken, t.getTokenHash()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Token has expired");
        }

        User user = userRepository.findByEmail(token.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenRepository.delete(token);

    }
}
