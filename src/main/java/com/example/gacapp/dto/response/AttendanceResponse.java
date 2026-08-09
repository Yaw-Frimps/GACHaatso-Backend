package com.example.gacapp.dto.response;

import com.example.gacapp.model.AttendanceStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponse {

    private String id;

    private String meetingId;

    private String memberId;

    private String memberName;

    private String leaderId;

    private String leaderName;

    private AttendanceStatus status;

    private String absenceReason;

    private LocalDateTime markedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}