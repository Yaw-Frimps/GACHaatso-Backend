package com.example.gacapp.dto.response;


import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetingScheduleResponse {

    private String id;

    private String name;

    private DayOfWeek dayOfWeek;

    private LocalTime meetingTime;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
