package com.example.gacapp.controller;

import com.example.gacapp.dto.response.ApiResponse;
import com.example.gacapp.dto.response.NotificationResponse;
import com.example.gacapp.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/notifications")
@RequiredArgsConstructor
public class NotificationController {


    private final NotificationService notificationService;



    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>>
    getNotifications(Pageable pageable){


        return ResponseEntity.ok(
                ApiResponse.success(
                        notificationService.getAdminNotifications(pageable),
                        "Notifications retrieved successfully"
                ));

    }



    @GetMapping("/unread-count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Long>>
    getUnreadCount(){


        return ResponseEntity.ok(
                ApiResponse.success(
                        notificationService.getUnreadCount(),
                        "Unread count retrieved successfully"
                ));

    }

}
