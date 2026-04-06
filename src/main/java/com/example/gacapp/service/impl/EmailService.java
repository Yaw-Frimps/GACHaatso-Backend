package com.example.gacapp.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
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

    // -----------------------------
    // PASSWORD RESET EMAIL
    // -----------------------------
    @Async
    public void sendPasswordResetEmail(String email, String resetLink) throws MessagingException {
        sendEmail(email, "Reset Your Password - GAC Bethel Center", "reset-password", context -> {
            context.setVariable("resetLink", resetLink);
            context.setVariable("year", LocalDate.now().getYear());
        });
    }

    // -----------------------------
    // LEADER APPROVAL EMAIL
    // -----------------------------
    @Async
    public void sendLeaderApprovalEmail(String email, String firstName) throws MessagingException {
        sendEmail(email, "Your Leader Account Has Been Approved", "leader-approved", context -> {
            context.setVariable("firstName", firstName);
            context.setVariable("status", "approved");
            context.setVariable("year", LocalDate.now().getYear());
        });
    }

    // -----------------------------
    // LEADER REJECTION EMAIL
    // -----------------------------
    @Async
    public void sendLeaderRejectionEmail(String email, String firstName) throws MessagingException {
        sendEmail(email, "Your Leader Account Has Been Rejected", "leader-rejected", context -> {
            context.setVariable("firstName", firstName);
            context.setVariable("status", "rejected");
            context.setVariable("year", LocalDate.now().getYear());
        });
    }

    // -----------------------------
    // PRIVATE GENERIC METHOD TO SEND EMAIL
    // -----------------------------
    private void sendEmail(String to, String subject, String templateName, EmailContextConfigurer configurer) throws MessagingException {
        log.info("Preparing email '{}' for {}", subject, to);

        try {
            // Prepare Thymeleaf context
            Context context = new Context();
            configurer.configure(context);

            String htmlContent = templateEngine.process(templateName, context);

            // Create MIME message
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email '{}' sent successfully to {}", subject, to);

        } catch (MailException e) {
            log.error("Failed to send email '{}' to {}: {}", subject, to, e.getMessage(), e);
            throw new MessagingException("Failed to send email due to mail server issue", e);
        } catch (Exception e) {
            log.error("Unexpected error while sending email '{}' to {}: {}", subject, to, e.getMessage(), e);
            throw new MessagingException("Unexpected error occurred while sending email", e);
        }
    }

    // -----------------------------
    // FUNCTIONAL INTERFACE FOR CONTEXT CONFIGURATION
    // -----------------------------
    @FunctionalInterface
    private interface EmailContextConfigurer {
        void configure(Context context);
    }
}