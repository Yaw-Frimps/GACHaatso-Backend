package com.example.gacapp.service;

import com.example.gacapp.dto.request.ContactMessageRequest;
import com.example.gacapp.dto.response.ContactMessageResponse;

import java.util.List;

/**
 * Service interface for handling contact message operations.
 * Defines methods for creating, retrieving and managing user contact messages.
 */
public interface ContactMessageService {

    /**
     * Creates and saves a new contact message based on the provided request.
     *
     * @param request The contact message details.
     * @return The saved contact message response.
     */
    ContactMessageResponse createContactMessage(ContactMessageRequest request);

    /**
     * Retrieves a specific contact message by its unique identifier.
     *
     * @param id The unique identifier of the contact message.
     * @return The corresponding contact message response.
     */
    ContactMessageResponse getContactMessageById(String id);

    /**
     * Retrieves all contact messages stored in the system.
     *
     * @return A list of all contact message responses.
     */
    List<ContactMessageResponse> getAllMessages();
}
