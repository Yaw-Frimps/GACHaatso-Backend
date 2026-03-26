package com.example.gacapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String token;
    private String role;
    private String createdAt;
    private String updatedAt;
}
