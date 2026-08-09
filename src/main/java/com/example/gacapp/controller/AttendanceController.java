package com.example.gacapp.controller;



import com.example.gacapp.dto.request.AttendanceRequest;
import com.example.gacapp.dto.request.BulkAttendanceRequest;
import com.example.gacapp.dto.response.ApiResponse;
import com.example.gacapp.dto.response.AttendanceReportResponse;
import com.example.gacapp.dto.response.AttendanceResponse;
import com.example.gacapp.dto.response.MeetingAttendanceSummaryResponse;
import com.example.gacapp.service.AttendanceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
@Tag(
        name = "Attendance",
        description = "Attendance management APIs for leaders and admins"
)
public class AttendanceController {



    private final AttendanceService attendanceService;



    // ======================================================
    // MARK ATTENDANCE
    // ======================================================


    @PostMapping
    @PreAuthorize("hasRole('LEADER')")
    @Operation(
            summary = "Mark member attendance",
            description =
                    "Allows a leader to mark a member as present or absent"
    )
    public ResponseEntity<ApiResponse<AttendanceResponse>> markAttendance(
            @Valid @RequestBody AttendanceRequest request
    ){


        AttendanceResponse response =
                attendanceService.markAttendance(request);



        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Attendance marked successfully"
                )
        );

    }





    // ======================================================
    // UPDATE ATTENDANCE
    // ======================================================


    @PatchMapping("/{attendanceId}")
    @PreAuthorize("hasRole('LEADER')")
    @Operation(
            summary = "Update attendance",
            description =
                    "Allows a leader to update an attendance record"
    )
    public ResponseEntity<ApiResponse<AttendanceResponse>> updateAttendance(
            @PathVariable String attendanceId,
            @Valid @RequestBody AttendanceRequest request
    ){


        AttendanceResponse response =
                attendanceService.updateAttendance(
                        attendanceId,
                        request
                );


        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Attendance updated successfully"
                )
        );

    }







    // ======================================================
    // GET SINGLE ATTENDANCE
    // ======================================================


    @GetMapping("/{attendanceId}")
    @Operation(
            summary = "Get attendance by id"
    )
    public ResponseEntity<ApiResponse<AttendanceResponse>> getAttendance(
            @PathVariable String attendanceId
    ){


        AttendanceResponse response =
                attendanceService.getAttendance(
                        attendanceId
                );


        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Attendance retrieved successfully"
                )
        );

    }







    // ======================================================
    // GET MEETING ATTENDANCE
    // ======================================================


    @GetMapping("/meeting/{meetingId}")
    @PreAuthorize(
            "hasAnyRole('ADMIN','LEADER')"
    )
    @Operation(
            summary = "Get meeting attendance",
            description =
                    "Retrieves attendance records for a meeting"
    )
    public ResponseEntity<ApiResponse<Page<AttendanceResponse>>>
    getMeetingAttendance(

            @PathVariable String meetingId,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size

    ){


        Pageable pageable =
                PageRequest.of(
                        page,
                        size
                );


        Page<AttendanceResponse> response =
                attendanceService.getMeetingAttendance(
                        meetingId,
                        pageable
                );


        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Meeting attendance retrieved successfully"
                )
        );

    }

    @PostMapping("/bulk")
    @PreAuthorize("hasRole('LEADER')")
    @Operation(
            summary="Bulk mark attendance"
    )
    public ResponseEntity<ApiResponse<Page<AttendanceResponse>>>
    bulkAttendance(
            @Valid @RequestBody BulkAttendanceRequest request
    ){


        return ResponseEntity.ok(

                ApiResponse.success(

                        attendanceService
                                .markBulkAttendance(request),

                        "Bulk attendance marked successfully"

                )

        );

    }








    // ======================================================
    // MEMBER ATTENDANCE HISTORY
    // ======================================================


    @GetMapping("/member/{memberId}")
    @PreAuthorize(
            "hasAnyRole('ADMIN','LEADER')"
    )
    @Operation(
            summary = "Get member attendance history"
    )
    public ResponseEntity<ApiResponse<Page<AttendanceResponse>>>
    getMemberAttendance(

            @PathVariable String memberId,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size

    ){


        Pageable pageable =
                PageRequest.of(
                        page,
                        size
                );



        Page<AttendanceResponse> response =
                attendanceService.getMemberAttendance(
                        memberId,
                        pageable
                );



        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Member attendance history retrieved successfully"
                )
        );

    }

    @GetMapping("/report/member/{memberId}")
    @PreAuthorize("hasAnyRole('ADMIN','LEADER')")
    public ResponseEntity<ApiResponse<AttendanceReportResponse>>
    memberReport(
            @PathVariable String memberId
    ){


        return ResponseEntity.ok(

                ApiResponse.success(

                        attendanceService
                                .getMemberAttendanceReport(memberId),

                        "Member attendance report retrieved"

                )

        );

    }


    @GetMapping("/report/meeting/{meetingId}")
    @PreAuthorize("hasAnyRole('ADMIN','LEADER')")
    public ResponseEntity<ApiResponse<MeetingAttendanceSummaryResponse>>
    meetingSummary(
            @PathVariable String meetingId
    ){


        return ResponseEntity.ok(

                ApiResponse.success(

                        attendanceService
                                .getMeetingSummary(meetingId),

                        "Meeting attendance summary retrieved"

                )

        );


    }

    @GetMapping("/report/monthly")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<AttendanceReportResponse>>>
    monthlyReport(

            @RequestParam int month,

            @RequestParam int year,

            @RequestParam(defaultValue="0")
            int page,

            @RequestParam(defaultValue="20")
            int size

    ){


        Pageable pageable =
                PageRequest.of(page,size);



        return ResponseEntity.ok(

                ApiResponse.success(

                        attendanceService
                                .getMonthlyReport(
                                        month,
                                        year,
                                        pageable
                                ),

                        "Monthly attendance report retrieved"

                )

        );


    }







    // ======================================================
    // LEADER ATTENDANCE REPORT
    // ======================================================


    @GetMapping("/my-records")
    @PreAuthorize("hasRole('LEADER')")
    @Operation(
            summary = "Get leader attendance records"
    )
    public ResponseEntity<ApiResponse<Page<AttendanceResponse>>>
    getMyAttendance(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size

    ){


        Pageable pageable =
                PageRequest.of(
                        page,
                        size
                );



        Page<AttendanceResponse> response =
                attendanceService.getLeaderAttendance(
                        pageable
                );



        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Attendance records retrieved successfully"
                )
        );

    }








    // ======================================================
    // DELETE ATTENDANCE
    // ======================================================


    @DeleteMapping("/{attendanceId}")
    @PreAuthorize("hasRole('LEADER')")
    @Operation(
            summary = "Delete attendance record"
    )
    public ResponseEntity<ApiResponse<Void>>
    deleteAttendance(

            @PathVariable String attendanceId

    ){


        attendanceService.deleteAttendance(
                attendanceId
        );


        return ResponseEntity.ok(
                ApiResponse.success(
                        null,
                        "Attendance deleted successfully"
                )
        );

    }


}