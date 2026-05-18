package com.example.gacapp.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ContactMessageRequest {
    @NotBlank(message = "Name is required")
    private String name;


    @Email(message = "Invalid email format")
    private String email;

    private String phone;

    @NotNull(message = "Message type is required")
    private String messageType;

    @NotBlank(message = "Message is required")
    private String message;
}
