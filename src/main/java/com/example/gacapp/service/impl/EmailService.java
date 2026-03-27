package com.example.gacapp.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    /**
     * Sends a password reset email to the specified recipient.
     *
     * @param email     Recipient email address
     * @param resetLink Password reset link
     * @throws MessagingException if the email could not be sent
     */
    public void sendPasswordResetEmail(String email, String resetLink) throws MessagingException {
        log.info("Preparing password reset email for {}", email);

        try {
            // Prepare the Thymeleaf email template
            Context context = new Context();
            context.setVariable("resetLink", resetLink);
            context.setVariable("year", LocalDate.now().getYear());
            String htmlContent = templateEngine.process("reset-password", context);

            // Create MIME message
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("Reset Your Password - GAC Bethel Center");
            helper.setText(htmlContent, true);

            // Send the email
            mailSender.send(message);
            log.info("Password reset email sent successfully to {}", email);

        } catch (MailException e) {
            // This handles SMTP connection issues and other mail sending errors
            log.error("Failed to send password reset email to {}: {}", email, e.getMessage(), e);
            throw new MessagingException("Failed to send email due to mail server issue", e);
        } catch (Exception e) {
            // Catch any other unexpected exceptions
            log.error("Unexpected error while sending password reset email to {}: {}", email, e.getMessage(), e);
            throw new MessagingException("Unexpected error occurred while sending email", e);
        }
    }
}