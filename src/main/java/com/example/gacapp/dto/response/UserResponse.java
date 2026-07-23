package com.example.gacapp.dto.response;


import com.example.gacapp.model.UserRole;
import lombok.Builder;
import lombok.Data;



@Data
@Builder
public class UserResponse {

    private String id;

    private String firstName;

    private String lastName;

    private String email;

    private UserRole role;

    private String approvalStatus;
}
