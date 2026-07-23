package com.example.gacapp.dto.response;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class BirthdayResponse {

    private String id;

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    private Integer age;

    private String phoneNumber;

    private String imageUrl;

}
