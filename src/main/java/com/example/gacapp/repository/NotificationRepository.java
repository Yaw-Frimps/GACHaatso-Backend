package com.example.gacapp.repository;


import com.example.gacapp.model.Notification;
import com.example.gacapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface NotificationRepository
        extends JpaRepository<Notification,String> {


    Page<Notification> findByRecipientOrderByCreatedAtDesc(
            User user,
            Pageable pageable
    );


    long countByRecipientAndReadFalse(
            User user
    );

}
