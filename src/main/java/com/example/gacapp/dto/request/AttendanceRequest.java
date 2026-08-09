package com.example.gacapp.dto.request;


import com.example.gacapp.model.AttendanceStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class AttendanceRequest {


    @NotBlank
    private String meetingId;


    @NotBlank
    private String memberId;


    @NotNull
    private AttendanceStatus status;


    private String absenceReason;

}