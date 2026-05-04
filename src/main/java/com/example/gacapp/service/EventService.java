package com.example.gacapp.service;

import com.example.gacapp.dto.request.EventRequest;
import com.example.gacapp.dto.response.EventResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EventService {
    EventResponse createEvent(EventRequest request);
    EventResponse getEventById(String eventId);
    List<EventResponse> getAllEvent();
    EventResponse updateEvent(String eventId, EventRequest request);
    void deleteEvent(String eventId);
}
