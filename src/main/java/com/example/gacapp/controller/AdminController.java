package com.example.gacapp.controller;

import com.example.gacapp.dto.response.ApiResponse;
import com.example.gacapp.dto.response.ApprovalStatusResponse;
import com.example.gacapp.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // ===========================
    // Get Pending Leaders (Paginated)
    // ===========================
    @GetMapping("/pending-leaders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<ApprovalStatusResponse>>> getPendingLeaders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ApprovalStatusResponse> pendingLeaders = adminService.getPendingLeaders(pageable);
        return ResponseEntity.ok(ApiResponse.success(pendingLeaders, "Pending leaders fetched successfully"));
    }

    @PatchMapping("/approve/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ApprovalStatusResponse>> approveLeader(@PathVariable String userId){
        ApprovalStatusResponse response = adminService.approveLeader(userId);
        return ResponseEntity.ok(ApiResponse.success(response, "Leader approved"));
    }


    @PatchMapping("/reject/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ApprovalStatusResponse>> rejectLeader(@PathVariable String userId){
        ApprovalStatusResponse response = adminService.rejectLeader(userId);
        return ResponseEntity.ok(ApiResponse.success(response, "Leader rejected"));
    }

    @DeleteMapping("/delete/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteLeader(@PathVariable String userId){
        adminService.deleteLeader(userId);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Leader deleted successfully")
        );
    }
}