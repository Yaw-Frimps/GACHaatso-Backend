package com.example.gacapp.dto.response;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Builder
public class NotificationResponse {


    private String id;


    private String title;


    private String message;


    private boolean read;


    private LocalDateTime createdAt;

}