package com.example.gacapp.service.impl;

import com.example.gacapp.dto.response.NotificationResponse;
import com.example.gacapp.model.Members;
import com.example.gacapp.model.Notification;
import com.example.gacapp.model.User;
import com.example.gacapp.model.UserRole;
import com.example.gacapp.repository.NotificationRepository;
import com.example.gacapp.repository.UserRepository;
import com.example.gacapp.service.NotificationService;
import com.example.gacapp.util.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl
        implements NotificationService {


    private final NotificationRepository notificationRepository;

    private final UserRepository userRepository;

    private final NotificationMapper notificationMapper;



    @Override
    public void createBirthdayNotifications(
            List<Members> members
    ){


        User admin =
                userRepository
                        .findFirstByRoleAndEnabledTrue(UserRole.ADMIN)
                        .orElseThrow();



        members.forEach(member -> {


            Notification notification =
                    Notification.builder()
                            .title("Birthday Reminder 🎂")
                            .message(
                                    member.getFirstName()
                                            +" "
                                            +member.getLastName()
                                            +" has a birthday today."
                            )
                            .recipient(admin)
                            .build();



            notificationRepository.save(notification);

        });

    }





    @Override
    public Page<NotificationResponse> getAdminNotifications(
            Pageable pageable
    ){

        User admin =
                getCurrentAdmin();


        return notificationRepository
                .findByRecipientOrderByCreatedAtDesc(
                        admin,
                        pageable
                )
                .map(notificationMapper::toDTO);

    }





    @Override
    public long getUnreadCount(){

        return notificationRepository
                .countByRecipientAndReadFalse(
                        getCurrentAdmin()
                );

    }





    private User getCurrentAdmin(){

        String email =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();


        return userRepository
                .findByEmail(email)
                .orElseThrow();

    }
}
