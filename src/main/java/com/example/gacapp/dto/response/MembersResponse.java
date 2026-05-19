package com.example.gacapp.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class MembersResponse {

    private String id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;

    private Integer age;

    private String gender;
    private String phoneNumber;
    private String email;
    private String maritalStatus;

    private String residenceAddress;
    private String occupation;
    private String emergencyNumber;

    private String imageUrl;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
