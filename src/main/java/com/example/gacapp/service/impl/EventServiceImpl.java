package com.example.gacapp.service.impl;

import com.example.gacapp.dto.request.EventRequest;
import com.example.gacapp.dto.response.EventResponse;
import com.example.gacapp.exception.EventNotFoundException;
import com.example.gacapp.model.Event;
import com.example.gacapp.repository.EventRepository;
import com.example.gacapp.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Override
    @CacheEvict(value = "events", allEntries = true)
    public EventResponse createEvent(EventRequest request) {
        log.info("Creating event");
        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .build();
        
        Event savedEvent = eventRepository.save(event);
        log.info("Saved event to database with id {}", savedEvent.getId());

        return mapToResponse(savedEvent);
    }

    @Override
    @Cacheable(value = "events", key = "#eventId")
    public EventResponse getEventById(String eventId) {
        log.info("Finding event with id {}", eventId);
        return eventRepository.findById(eventId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + eventId));
    }

    @Override
    @Cacheable(value = "events", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<EventResponse> getAllEvent(Pageable pageable) {
        log.info("Retrieving paginated events");
        return eventRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @CacheEvict(value = "events", allEntries = true)
    public EventResponse updateEvent(String eventId, EventRequest request) {
        log.info("Updating event with id {}", eventId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found with Id: " + eventId));

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());

        return mapToResponse(eventRepository.save(event));
    }

    @Override
    @CacheEvict(value = "events", allEntries = true)
    public void deleteEvent(String eventId) {
        log.info("Deleting event with id {}", eventId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        eventRepository.delete(event);
    }

    private EventResponse mapToResponse(Event event){
        return EventResponse.builder()
                .userId(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }
}
