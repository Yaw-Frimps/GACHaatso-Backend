package com.example.gacapp.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BulkAttendanceRequest {

    @NotBlank
    private String meetingId;

    @Valid
    @NotEmpty(message = "Attendance list cannot be empty")
    private List<AttendanceRequest> attendance;

}