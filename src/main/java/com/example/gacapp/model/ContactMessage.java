package com.example.gacapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entity class representing a contact message sent by a user.
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ContactMessage {
    /**
     * Unique identifier for the contact message.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * Name of the person who sent the message.
     */
    @NotBlank(message = "Name is required")
    private String name;

    /**
     * Email address of the person who sent the message.
     */
    private String email;

    /**
     * Phone number of the person who sent the message.
     */
    @NotBlank(message = "Phone number is required")
    private String phone;

    /**
     * Type of message (e.g., INQUIRY, FEEDBACK, etc.).
     */
    private String messageType;

    /**
     * The actual content of the message.
     */
    @NotBlank(message = "Message is required")
    private String message;

    /**
     * Timestamp when the message was created.
     */
    @CreatedDate
    private LocalDateTime createdAt;

    /**
     * Timestamp when the message was last updated.
     */
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
