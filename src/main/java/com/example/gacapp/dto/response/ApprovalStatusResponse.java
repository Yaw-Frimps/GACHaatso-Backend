package com.example.gacapp.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalStatusResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String role;
    private String approvalStatus;
    private LocalDateTime approvedAt;
}
