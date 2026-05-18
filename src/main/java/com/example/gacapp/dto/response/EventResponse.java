package com.example.gacapp.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EventResponse {
    private String userId;
    private String title;
    private String description;
    private String location;
    private LocalDateTime date;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
