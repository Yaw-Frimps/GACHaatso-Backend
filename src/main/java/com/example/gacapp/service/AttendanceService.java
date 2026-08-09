package com.example.gacapp.service;


import com.example.gacapp.dto.request.AttendanceRequest;
import com.example.gacapp.dto.request.BulkAttendanceRequest;
import com.example.gacapp.dto.response.AttendanceReportResponse;
import com.example.gacapp.dto.response.AttendanceResponse;

import com.example.gacapp.dto.response.MeetingAttendanceSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface AttendanceService {


    AttendanceResponse markAttendance(
            AttendanceRequest request
    );


    Page<AttendanceResponse> markBulkAttendance(
            BulkAttendanceRequest request
    );


    AttendanceResponse updateAttendance(
            String attendanceId,
            AttendanceRequest request
    );


    AttendanceResponse getAttendance(
            String attendanceId
    );


    Page<AttendanceResponse> getMeetingAttendance(
            String meetingId,
            Pageable pageable
    );


    Page<AttendanceResponse> getMemberAttendance(
            String memberId,
            Pageable pageable
    );


    Page<AttendanceResponse> getLeaderAttendance(
            Pageable pageable
    );


    void deleteAttendance(
            String attendanceId
    );

    AttendanceReportResponse getMemberAttendanceReport(
            String memberId
    );



    Page<AttendanceReportResponse> getLeaderAttendanceReport(
            Pageable pageable
    );



    MeetingAttendanceSummaryResponse getMeetingSummary(
            String meetingId
    );



    Page<AttendanceReportResponse> getMonthlyReport(
            int month,
            int year,
            Pageable pageable
    );

}