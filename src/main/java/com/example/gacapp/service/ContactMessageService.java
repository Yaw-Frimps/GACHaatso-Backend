package com.example.gacapp.service;

import com.example.gacapp.dto.request.ContactMessageRequest;
import com.example.gacapp.dto.response.ContactMessageResponse;
import com.example.gacapp.model.ContactMessage;
import com.example.gacapp.repository.ContactMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContactMessageService {
    private final ContactMessageRepository contactMessageRepository;

    public ContactMessageResponse createContactMessage(ContactMessageRequest request){
        ContactMessage contactMessage = ContactMessage.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .messageType(request.getMessageType())
                .message(request.getMessage())
                .build();

        return mapToResponse(contactMessageRepository.save(contactMessage));
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
