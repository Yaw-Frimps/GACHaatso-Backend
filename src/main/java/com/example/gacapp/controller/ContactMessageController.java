package com.example.gacapp.controller;

import com.example.gacapp.dto.request.ContactMessageRequest;
import com.example.gacapp.dto.response.ApiResponse;
import com.example.gacapp.dto.response.ContactMessageResponse;
import com.example.gacapp.service.ContactMessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class ContactMessageController {

    private final ContactMessageService contactMessageService;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<ContactMessageResponse>> createContactMessage(@Valid @RequestBody ContactMessageRequest request){
        ContactMessageResponse response = contactMessageService.createContactMessage(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Contact message sent successfully"));
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<ContactMessageResponse>>> getAllMessages(){
        List<ContactMessageResponse> messages = contactMessageService.getAllMessages();
        return ResponseEntity.ok(ApiResponse.success(messages, "Messages retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ContactMessageResponse>> getMessageById(@PathVariable String id){
        ContactMessageResponse response = contactMessageService.getMessageById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Message retrieved successfully"));
    }
}
