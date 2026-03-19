package com.example.gacapp.dto.response;

import com.example.gacapp.model.MessageType;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

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
