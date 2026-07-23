package com.example.gacapp.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeaderResponse {

    private String id;

    private String firstName;

    private String lastName;

    private String email;

}