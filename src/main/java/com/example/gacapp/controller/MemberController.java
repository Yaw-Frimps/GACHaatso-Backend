package com.example.gacapp.controller;

import com.example.gacapp.dto.request.MembersRequest;
import com.example.gacapp.dto.response.ApiResponse;
import com.example.gacapp.dto.response.MembersResponse;
import com.example.gacapp.service.MemberService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@Tag(
        name = "Members",
        description = "APIs for managing church members"
)
public class MemberController {

    private final MemberService service;
    private final ObjectMapper objectMapper;

    @PostMapping(
            value = "/create-member",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create a new member",
            description = "Creates a member profile with optional profile image upload"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Member created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<MembersResponse>> createMember(
            @RequestPart("member") String memberJson,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws JsonProcessingException {

        MembersRequest request =
                objectMapper.readValue(memberJson, MembersRequest.class);

        MembersResponse response =
                service.createMember(request, file);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Member created successfully")
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get member by ID")
    public ResponseEntity<ApiResponse<MembersResponse>> getMemberById(
            @PathVariable String id
    ) {
        MembersResponse response = service.getMemberById(id);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Member retrieved successfully")
        );
    }

    @GetMapping
    @Operation(summary = "Get all members (paginated)")
    public ResponseEntity<ApiResponse<Page<MembersResponse>>> getAllMembers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        Page<MembersResponse> response = service.getAllMembers(pageable);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Members retrieved successfully")
        );
    }

    @PatchMapping(
            value = "/update-member/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(summary = "Update member details")
    public ResponseEntity<ApiResponse<MembersResponse>> updateMember(
            @PathVariable String id,
            @RequestPart("member") String memberJson,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws JsonProcessingException {

        MembersRequest request =
                objectMapper.readValue(memberJson, MembersRequest.class);

        MembersResponse response =
                service.updateMember(id, request, file);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Member updated successfully")
        );
    }

    @DeleteMapping("/delete-member/{id}")
    @Operation(summary = "Delete a member")
    public ResponseEntity<ApiResponse<Void>> deleteMember(
            @PathVariable String id
    ) {

        service.deleteMember(id);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Member deleted successfully")
        );
    }
}