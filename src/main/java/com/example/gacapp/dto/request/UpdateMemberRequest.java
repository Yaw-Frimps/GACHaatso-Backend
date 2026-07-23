package com.example.gacapp.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UpdateMemberRequest {

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    @Size(max = 10)
    private String gender;

    @Pattern(regexp = "^\\+?\\d{7,15}$", message = "Invalid phone number")
    private String phoneNumber;

    @Email(message = "Invalid email")
    private String email;

    private String maritalStatus;

    private String residenceAddress;

    private String occupation;

    @Pattern(regexp = "^\\+?\\d{7,15}$", message = "Invalid phone number")
    private String emergencyNumber;
}
