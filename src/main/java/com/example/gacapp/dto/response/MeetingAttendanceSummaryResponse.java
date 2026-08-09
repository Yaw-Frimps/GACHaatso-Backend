package com.example.gacapp.dto.response;


import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetingAttendanceSummaryResponse {


    private String meetingId;


    private long totalMarked;


    private long present;


    private long absent;

}
