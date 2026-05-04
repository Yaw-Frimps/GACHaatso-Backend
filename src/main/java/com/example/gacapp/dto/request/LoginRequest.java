package com.example.gacapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

/**
 * Data Transfer Object for login requests.
 */
@Data
@Builder
public class LoginRequest {
    /**
     * The user's email address.
     */
    @NotBlank(message = "Email cannot be empty")
    private String email;

    /**
     * The user's password.
     */
    @NotBlank(message = "Password cannot be empty")
    private String password;
}
