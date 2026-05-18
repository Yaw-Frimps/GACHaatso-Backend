package com.example.gacapp.controller;

import com.example.gacapp.dto.request.ContactMessageRequest;
import com.example.gacapp.dto.response.ApiResponse;
import com.example.gacapp.dto.response.ContactMessageResponse;
import com.example.gacapp.service.ContactMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * Controller for handling contact message operations.
 * Provides endpoints for users to send and administrators to view contact messages.
 */
@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
@Tag(
        name = "Contact Messages",
        description = "APIs for managing contact messages from users"
)
public class ContactMessageController {

    private final ContactMessageService contactMessageService;

    /**
     * Endpoint to receive and process a contact message.
     *
     * @param request The contact message details from the user.
     * @return A standardized response containing the created message details.
     */
    @PostMapping("/send")
    @Operation(
            summary = "Send a message using the contact forms",
            description = "This allows users to submit messages or requests using the contact message form to the system. The message will be stored and can be reviewed by administrators."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Message sent successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<ContactMessageResponse>> createContactMessage(
            @Valid @RequestBody ContactMessageRequest request){
        ContactMessageResponse response = contactMessageService.createContactMessage(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Contact message sent successfully"));
    }

    /**
     * Endpoint to retrieve all contact messages.
     * Accessible only by users with both ADMIN and LEADER roles.
     *
     * @return A list of all contact messages wrapped in a standard API response.
     */
    @GetMapping()
    @PreAuthorize("hasAnyRole('ADMIN','LEADER')")
    @Operation(
            summary = "Get all contact messages",
            description = "Retrieves paginated list of all contact messages. Restricted to ADMIN and LEADER roles."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Messages retrieved successfully"),
           @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
           @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<Page<ContactMessageResponse>>> getAllMessages(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of records per page", example = "10")
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        Page<ContactMessageResponse> messages = contactMessageService.getAllMessages(pageable);
        return ResponseEntity.ok(ApiResponse.success(messages, "Messages retrieved successfully"));
    }

    /**
     * Endpoint to retrieve a single contact message by its ID.
     * Accessible only by users with both ADMIN and LEADER roles.
     *
     * @param id The ID of the contact message to retrieve.
     * @return The contact message details wrapped in a standard API response.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LEADER')")
    @Operation(
            summary = "Get contact message by ID",
            description = "Fetches a single contact message using its unique identifier. Restricted to ADMIN and LEADER roles."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Message retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Message not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<ContactMessageResponse>> getMessageById(@PathVariable String id){
        ContactMessageResponse response = contactMessageService.getContactMessageById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Message retrieved successfully"));
    }
}
