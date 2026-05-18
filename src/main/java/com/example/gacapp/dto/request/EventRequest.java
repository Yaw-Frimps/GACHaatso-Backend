package com.example.gacapp.dto.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EventRequest {
    private String title;
    private String description;
    private String location;
    private LocalDateTime date;
    private String imageUrl;

}
