package com.example.gacapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(name = "members")
@EntityListeners(AuditingEntityListener.class)
public class Members {
    public static final String PHONE_REGEX = "^\\+?\\d{7,15}$";


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    @Size(max = 10)
    private String gender;

    @Column(unique = true)
    @Pattern(regexp = PHONE_REGEX, message = "Invalid phone number")
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @Column(unique = true)
    @Email(message = "Email should be valid")
    private String email;

    private String maritalStatus;

    @Column(columnDefinition = "TEXT")
    private String residenceAddress;

    private String occupation;

    @Pattern(regexp = PHONE_REGEX, message = "Invalid phone number")
    private String emergencyNumber;

    @Column(length = 1000, name = "image_url")
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "leader_id", nullable = false)
    private User leader;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Transient
    public Integer getAge() {
        if (dateOfBirth == null) return null;
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }
}
