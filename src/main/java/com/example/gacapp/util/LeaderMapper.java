package com.example.gacapp.util;

import com.example.gacapp.dto.response.LeaderResponse;
import com.example.gacapp.model.User;
import org.springframework.stereotype.Component;


@Component
public class LeaderMapper {


    public LeaderResponse toDTO(User leader){


        if(leader == null){
            return null;
        }


        return LeaderResponse.builder()
                .id(leader.getId())
                .firstName(leader.getFirstName())
                .lastName(leader.getLastName())
                .email(leader.getEmail())
                .build();

    }

}