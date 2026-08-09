package com.example.gacapp.controller;

import com.example.gacapp.dto.request.MeetingScheduleRequest;
import com.example.gacapp.dto.response.ApiResponse;
import com.example.gacapp.dto.response.MeetingScheduleResponse;
import com.example.gacapp.service.MeetingScheduleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/meeting-schedules")
@RequiredArgsConstructor
@Tag(
        name = "Meeting Schedules",
        description = "Admin meeting schedule management"
)
public class MeetingScheduleController {

    private final MeetingScheduleService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MeetingScheduleResponse>> create(

            @RequestBody @Valid
            MeetingScheduleRequest request

    ){

        return ResponseEntity.ok(

                ApiResponse.success(

                        service.create(request),

                        "Meeting schedule created successfully"

                )

        );

    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MeetingScheduleResponse>> update(

            @PathVariable String id,

            @RequestBody MeetingScheduleRequest request

    ){

        return ResponseEntity.ok(

                ApiResponse.success(

                        service.update(id,request),

                        "Meeting schedule updated successfully"

                )

        );

    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivate(

            @PathVariable String id

    ){

        service.deactivate(id);

        return ResponseEntity.ok(

                ApiResponse.success(

                        null,

                        "Meeting schedule deactivated successfully"

                )

        );

    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MeetingScheduleResponse>>> getAll(){

        return ResponseEntity.ok(

                ApiResponse.success(

                        service.getAll(),

                        "Meeting schedules retrieved successfully"

                )

        );

    }

}