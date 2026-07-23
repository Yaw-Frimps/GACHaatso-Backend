package com.example.gacapp.util;

import com.example.gacapp.dto.response.LeaderResponse;
import com.example.gacapp.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LeaderMapper {

    public LeaderResponse toDTO(User leader) {
        if (leader == null) return null;

        return LeaderResponse.builder()
                .id(leader.getId())
                .firstName(leader.getFirstName())
                .lastName(leader.getLastName())
                .email(leader.getEmail())
                .build();
    }
}
