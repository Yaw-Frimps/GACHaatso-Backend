package com.example.gacapp.service;

import com.example.gacapp.dto.request.ContactMessageRequest;
import com.example.gacapp.dto.response.ContactMessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
     * Retrieves all contact messages stored in the system (paginated).
     *
     * @param pageable The pagination information.
     * @return A page of contact message responses.
     */
    Page<ContactMessageResponse> getAllMessages(Pageable pageable);
}
