package com.example.gacapp.dto.response;


import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetingResponse {

    private String id;

    private String scheduleName;

    private LocalDate meetingDate;

    private LocalTime meetingTime;

    private boolean attendanceClosed;

    private LocalDateTime createdAt;

}