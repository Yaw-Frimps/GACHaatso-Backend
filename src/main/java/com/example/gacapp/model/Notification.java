package com.example.gacapp.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notifications")
public class Notification {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;


    private String title;


    @Column(columnDefinition = "TEXT")
    private String message;


    private boolean read;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User recipient;


    private LocalDateTime createdAt;


    @PrePersist
    public void prePersist(){

        createdAt = LocalDateTime.now(ZoneOffset.UTC);

        read = false;

    }

}