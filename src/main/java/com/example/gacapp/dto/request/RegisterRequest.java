package com.example.gacapp.dto.request;

import com.example.gacapp.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/**
 * Data Transfer Object for user registration requests.
 */
@Data
@Builder
public class RegisterRequest {
    /**
     * The user's first name.
     */
    @NotBlank(message = "First name cannot be empty")
    private String firstName;

    /**
     * The user's last name.
     */
    @NotBlank(message = "Last name cannot be empty")
    private String lastName;

    /**
     * The user's email address. Must be a valid email format.
     */
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String email;

    /**
     * The user's password. Must be between 6 and 20 characters long.
     */
    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    private String password;

    private UserRole role;
}
