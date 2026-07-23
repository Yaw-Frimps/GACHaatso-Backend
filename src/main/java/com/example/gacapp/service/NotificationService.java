package com.example.gacapp.service;



import com.example.gacapp.dto.response.NotificationResponse;
import com.example.gacapp.model.Members;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface NotificationService {


    Page<NotificationResponse> getAdminNotifications(
            Pageable pageable
    );


    long getUnreadCount();

    void createBirthdayNotifications(
            List<Members> members
    );

}