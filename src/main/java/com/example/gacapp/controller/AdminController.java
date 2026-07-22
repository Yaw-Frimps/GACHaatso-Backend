package com.example.gacapp.controller;

import com.example.gacapp.dto.response.ApiResponse;
import com.example.gacapp.dto.response.ApprovalStatusResponse;
import com.example.gacapp.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "Administrator",
        description = "APIs for managing Admin activities"
)
public class AdminController {

    private final AdminService adminService;

    // ===========================
    // Get Pending Leaders (Paginated)
    // ===========================
    @GetMapping("/pending-leaders")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get Pending Leaders",
            description = "This allows the Admin to retrieve all pending leaders."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pending Leaders fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorised"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<Page<ApprovalStatusResponse>>> getPendingLeaders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ApprovalStatusResponse> pendingLeaders = adminService.getPendingUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success(pendingLeaders, "Pending leaders fetched successfully"));
    }

    @GetMapping("/approved-leaders")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get Approved Leaders",
            description = "This allows the Admin to retrieve all the approved leaders."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Approved leaders fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorised"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<Page<ApprovalStatusResponse>>> getApprovedLeaders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ApprovalStatusResponse> approvedLeaders = adminService.getApprovedUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success(approvedLeaders, "Approved leaders fetched successfully"));
    }


    @PatchMapping("/approve/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Approve Leader",
            description = "This allows the Admin to approve a leader who has signed up before they get access to their dashboard."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Leader approved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorised"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<ApprovalStatusResponse>> approveLeader(@PathVariable String userId){
        ApprovalStatusResponse response = adminService.approveUser(userId);
        return ResponseEntity.ok(ApiResponse.success(response, "Leader approved successfully"));
    }


    @PatchMapping("/reject/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Reject Leader",
            description = "This allows the Admin to reject a leader."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Leader rejected successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorised"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<ApprovalStatusResponse>> rejectLeader(@PathVariable String userId){
        ApprovalStatusResponse response = adminService.rejectUser(userId);
        return ResponseEntity.ok(ApiResponse.success(response, "Leader rejected successfully"));
    }

    @DeleteMapping("/delete/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete Leader",
            description = "This allows the Admin to delete a leader's account."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Leader's account deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorised"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<Void>> deleteLeader(@PathVariable String userId){
        adminService.deleteUser(userId);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Leader's account deleted successfully")
        );
    }
}