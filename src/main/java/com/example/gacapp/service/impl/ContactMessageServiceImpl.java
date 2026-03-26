package com.example.gacapp.service.impl;

import com.example.gacapp.dto.request.ContactMessageRequest;
import com.example.gacapp.dto.response.ContactMessageResponse;
import com.example.gacapp.model.ContactMessage;
import com.example.gacapp.repository.ContactMessageRepository;
import com.example.gacapp.service.ContactMessageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactMessageServiceImpl implements ContactMessageService {
    private final ContactMessageRepository contactMessageRepository;

    @Transactional
    public ContactMessageResponse createContactMessage(ContactMessageRequest request){
        log.info("Creating contact message from: {} with email: {}", request.getName(), request.getEmail());
        ContactMessage contactMessage = ContactMessage.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .messageType(request.getMessageType())
                .message(request.getMessage())
                .build();

        log.info("Saving contact message to database for: {}", request.getEmail());
        return mapToResponse(contactMessageRepository.save(contactMessage));
    }


    @Transactional
    public List<ContactMessageResponse> getAllMessages() {
        log.info("Retrieving all contact messages from database");
        return contactMessageRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public ContactMessageResponse getContactMessageById(String id) {
        log.info("Retrieving contact message with id: {}", id);
        ContactMessage contactMessage = contactMessageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        return mapToResponse(contactMessage);
    }

    private ContactMessageResponse mapToResponse(ContactMessage contactMessage){
        return ContactMessageResponse.builder()
                .id(contactMessage.getId())
                .name(contactMessage.getName())
                .email(contactMessage.getEmail())
                .phone(contactMessage.getPhone())
                .messageType(contactMessage.getMessageType().name())
                .message(contactMessage.getMessage())
                .createdAt(contactMessage.getCreatedAt())
                .updatedAt(contactMessage.getUpdatedAt())
                .build();
    }
}
