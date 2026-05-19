package com.example.gacapp.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MembersRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    private String gender;

    @Pattern(regexp = "^\\+?\\d{7,15}$", message = "Invalid phone number")
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @Email(message = "Invalid email")
    private String email;

    private String maritalStatus;

    private String residenceAddress;

    private String occupation;

    @Pattern(regexp = "^\\+?\\d{7,15}$", message = "Invalid phone number")
    private String emergencyNumber;
}
