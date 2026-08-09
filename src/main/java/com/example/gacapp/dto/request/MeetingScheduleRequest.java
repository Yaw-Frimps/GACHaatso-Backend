package com.example.gacapp.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingScheduleRequest {

    @NotBlank
    private String name;

    @NotNull
    private DayOfWeek dayOfWeek;

    @NotNull
    private LocalTime meetingTime;

}