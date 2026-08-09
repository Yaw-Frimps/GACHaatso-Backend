package com.example.gacapp.controller;

import com.example.gacapp.dto.response.ApiResponse;
import com.example.gacapp.dto.response.MeetingResponse;
import com.example.gacapp.service.MeetingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/meetings")
@RequiredArgsConstructor
@Tag(
        name = "Meetings",
        description = "Meeting management APIs"
)
public class MeetingController {

    private final MeetingService meetingService;

    @GetMapping("/upcoming")
    @Operation(
            summary = "Retrieve upcoming meetings"
    )
    @PreAuthorize("hasAnyRole('ADMIN','LEADER')")
    public ResponseEntity<ApiResponse<Page<MeetingResponse>>> upcoming(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size
    ){

        Pageable pageable =
                PageRequest.of(page,size);

        return ResponseEntity.ok(

                ApiResponse.success(

                        meetingService.getUpcomingMeetings(pageable),

                        "Upcoming meetings retrieved successfully"

                )

        );

    }

    @GetMapping("/history")
    @Operation(
            summary = "Retrieve previous meetings"
    )
    public ResponseEntity<ApiResponse<Page<MeetingResponse>>> history(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size
    ){

        Pageable pageable =
                PageRequest.of(page,size);

        return ResponseEntity.ok(

                ApiResponse.success(

                        meetingService.getPastMeetings(pageable),

                        "Meeting history retrieved successfully"

                )

        );

    }

    @PatchMapping("/{meetingId}/close-attendance")
    @PreAuthorize("hasAnyRole('ADMIN','LEADER')")
    @Operation(
            summary="Close attendance"
    )
    public ResponseEntity<ApiResponse<Void>>
    closeAttendance(
            @PathVariable String meetingId
    ){


        meetingService.closeAttendance(
                meetingId
        );


        return ResponseEntity.ok(

                ApiResponse.success(
                        null,
                        "Attendance closed successfully"
                )

        );

    }

}
