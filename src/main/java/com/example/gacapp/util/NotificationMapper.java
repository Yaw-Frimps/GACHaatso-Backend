package com.example.gacapp.util;

import com.example.gacapp.dto.response.NotificationResponse;
import com.example.gacapp.model.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationMapper {


    public NotificationResponse toDTO(Notification notification) {

        if (notification == null) return null;


        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}