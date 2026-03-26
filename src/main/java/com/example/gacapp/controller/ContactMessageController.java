package com.example.gacapp.controller;

import com.example.gacapp.dto.request.ContactMessageRequest;
import com.example.gacapp.dto.response.ApiResponse;
import com.example.gacapp.dto.response.ContactMessageResponse;
import com.example.gacapp.service.ContactMessageService;
import com.example.gacapp.service.impl.ContactMessageServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class ContactMessageController {

    private final ContactMessageService contactMessageService;

    @PostMapping("/send")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<ApiResponse<ContactMessageResponse>> createContactMessage(@Valid @RequestBody ContactMessageRequest request){
        ContactMessageResponse response = contactMessageService.createContactMessage(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Contact message sent successfully"));
    }

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ContactMessageResponse>>> getAllMessages(){
        List<ContactMessageResponse> messages = contactMessageService.getAllMessages();
        return ResponseEntity.ok(ApiResponse.success(messages, "Messages retrieved successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ContactMessageResponse>> getMessageById(@PathVariable String id){
        ContactMessageResponse response = contactMessageService.getContactMessageById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Message retrieved successfully"));
    }


}
