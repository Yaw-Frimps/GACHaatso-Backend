package com.example.gacapp.dto.response;



import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceReportResponse {


    private String memberId;

    private String memberName;


    private long totalMeetings;


    private long present;


    private long absent;


    private double attendancePercentage;

}