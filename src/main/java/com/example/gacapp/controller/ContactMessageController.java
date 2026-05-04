package com.example.gacapp.controller;

import com.example.gacapp.dto.request.ContactMessageRequest;
import com.example.gacapp.dto.response.ApiResponse;
import com.example.gacapp.dto.response.ContactMessageResponse;
import com.example.gacapp.service.ContactMessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for handling contact message operations.
 * Provides endpoints for users to send and administrators to view contact messages.
 */
@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class ContactMessageController {

    private final ContactMessageService contactMessageService;

    /**
     * Endpoint to receive and process a contact message.
     *
     * @param request The contact message details from the user.
     * @return A standardized response containing the created message details.
     */
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<ContactMessageResponse>> createContactMessage(@Valid @RequestBody ContactMessageRequest request){
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
    @PreAuthorize("hasRole('ADMIN') && hasRole('LEADER')")
    public ResponseEntity<ApiResponse<List<ContactMessageResponse>>> getAllMessages(){
        List<ContactMessageResponse> messages = contactMessageService.getAllMessages();
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
    @PreAuthorize("hasRole('ADMIN') && hasRole('LEADER')")
    public ResponseEntity<ApiResponse<ContactMessageResponse>> getMessageById(@PathVariable String id){
        ContactMessageResponse response = contactMessageService.getContactMessageById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Message retrieved successfully"));
    }
}
