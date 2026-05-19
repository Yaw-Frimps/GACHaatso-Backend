package com.example.gacapp.service;

import com.example.gacapp.dto.request.EventRequest;
import com.example.gacapp.dto.response.EventResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface EventService {
    EventResponse createEvent(EventRequest request, MultipartFile file);
    EventResponse getEventById(String eventId);
    Page<EventResponse> getAllEvent(Pageable pageable);
    EventResponse updateEvent(String eventId, EventRequest request, MultipartFile file);
    void deleteEvent(String eventId);
}
