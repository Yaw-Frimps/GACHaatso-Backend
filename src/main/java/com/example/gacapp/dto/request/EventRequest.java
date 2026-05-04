package com.example.gacapp.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventRequest {
    private String title;
    private String description;
}
