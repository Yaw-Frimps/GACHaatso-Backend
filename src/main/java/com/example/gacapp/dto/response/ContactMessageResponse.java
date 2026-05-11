package com.example.gacapp.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ContactMessageResponse {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String messageType;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
