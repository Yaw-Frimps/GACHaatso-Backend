package com.example.gacapp.util;

import com.example.gacapp.dto.response.UserResponse;
import com.example.gacapp.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    public UserResponse toDTO(User user) {
        if (user == null) return null;

        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .approvalStatus(String.valueOf(user.getApprovalStatus()))
                .build();
    }
}
