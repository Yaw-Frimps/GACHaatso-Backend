package com.example.gacapp.service;

import com.example.gacapp.dto.request.ContactMessageRequest;
import com.example.gacapp.dto.response.ContactMessageResponse;

import java.util.List;

public interface ContactMessageService {
    ContactMessageResponse createContactMessage(ContactMessageRequest request);
    ContactMessageResponse getContactMessageById(String id);
    List<ContactMessageResponse> getAllMessages();

}
