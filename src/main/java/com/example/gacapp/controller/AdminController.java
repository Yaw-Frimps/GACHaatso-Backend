package com.example.gacapp.controller;

import com.example.gacapp.dto.response.ApiResponse;
import com.example.gacapp.dto.response.ApprovalStatusResponse;
import com.example.gacapp.dto.response.MembersResponse;
import com.example.gacapp.dto.response.UserResponse;
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
    @GetMapping("/pending-users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get Pending Users",
            description = "This allows the Admin to retrieve all pending users."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pending Users fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorised"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<Page<ApprovalStatusResponse>>> getPendingUsers(
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
            summary = "Get Approved Users",
            description = "This allows the Admin to retrieve all the approved users."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Approved users fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorised"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<Page<ApprovalStatusResponse>>> getApprovedUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ApprovalStatusResponse> approvedUsers = adminService.getApprovedUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success(approvedUsers, "Approved users fetched successfully"));
    }


    @PatchMapping("/approve/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Approve User",
            description = "This allows the Admin to approve a user who has signed up before they get access to their dashboard."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User approved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorised"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<ApprovalStatusResponse>> approveUser(@PathVariable String userId){
        ApprovalStatusResponse response = adminService.approveUser(userId);
        return ResponseEntity.ok(ApiResponse.success(response, "User approved successfully"));
    }


    @PatchMapping("/reject/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Reject User",
            description = "This allows the Admin to reject a user."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User rejected successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorised"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<ApprovalStatusResponse>> rejectUser(@PathVariable String userId){
        ApprovalStatusResponse response = adminService.rejectUser(userId);
        return ResponseEntity.ok(ApiResponse.success(response, "User rejected successfully"));
    }

    @DeleteMapping("/delete/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete User",
            description = "This allows the Admin to delete a user's account."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User's account deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorised"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String userId){
        adminService.deleteUser(userId);
        return ResponseEntity.ok(
                ApiResponse.success(null, "User's account deleted successfully")
        );
    }


    @PatchMapping("/members/{memberId}/assign/{leaderId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Assign Leader to Member",
            description = "This allows the Admin to assign an approved Leader to a member."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Leader assigned successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorised"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Member or Leader not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<Void>> assignLeaderToMember(
            @PathVariable String memberId,
            @PathVariable String leaderId
    ) {
        adminService.assignMemberToLeader(memberId, leaderId);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Leader assigned to member successfully")
        );
    }


    @GetMapping("/leaders/{leaderId}/members")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<MembersResponse>>> getLeaderMembers(
            @PathVariable String leaderId,
            Pageable pageable
    ){

        Page<MembersResponse> leadersMembers = adminService.getLeaderMembers(
                leaderId,
                pageable
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        leadersMembers,
                        "Leader members fetched successfully"
                )
        );
    }

    @GetMapping("/members/unassigned")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<MembersResponse>>> getUnassignedMembers(
            Pageable pageable
    ){

        Page<MembersResponse> unassignedMembers =
                adminService.getUnassignedMembers(pageable);

        return ResponseEntity.ok(
                ApiResponse.success(
                        unassignedMembers,
                        "Unassigned members fetched successfully"
                )
        );
    }

    @GetMapping("/leaders/available")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAvailableLeaders(
            Pageable pageable
    ){

        Page<UserResponse> availableLeaders =
                adminService.getAvailableLeaders(pageable);

        return ResponseEntity.ok(
                ApiResponse.success(
                        availableLeaders,
                        "Available leaders fetched successfully"
                )
        );
    }
}