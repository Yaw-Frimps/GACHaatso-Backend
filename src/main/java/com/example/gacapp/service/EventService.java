package com.example.gacapp.service;

import com.example.gacapp.dto.request.EventRequest;
import com.example.gacapp.dto.response.EventResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface EventService {
    EventResponse createEvent(EventRequest request);
    EventResponse getEventById(String eventId);
    Page<EventResponse> getAllEvent(Pageable pageable);
    EventResponse updateEvent(String eventId, EventRequest request);
    void deleteEvent(String eventId);
}
