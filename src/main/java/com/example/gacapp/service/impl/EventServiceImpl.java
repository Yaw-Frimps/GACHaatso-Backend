package com.example.gacapp.service.impl;

import com.example.gacapp.dto.request.EventRequest;
import com.example.gacapp.dto.response.EventResponse;
import com.example.gacapp.exception.EventNotFoundException;
import com.example.gacapp.model.Event;
import com.example.gacapp.repository.EventRepository;
import com.example.gacapp.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    @Override
    public EventResponse createEvent(EventRequest request) {
        log.info("Creating event");
        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .build();
        log.info("Saving event to database with id {}", event.getId());

        return mapToResponse(eventRepository.save(event));
    }

    @Override
    public EventResponse getEventById(String eventId) {
        log.info("Finding event with id {}", eventId);
        EventResponse response = eventRepository.findById(eventId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + eventId));
        log.info("Event with id {} found successfully", eventId);
        return response;
    }

    @Override
    public List<EventResponse> getAllEvent() {
        log.info("Retrieving all events created in the database");

        return eventRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public EventResponse updateEvent(String eventId, EventRequest request) {
        log.info("Updating event with id {}", eventId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found with Id: " + eventId));

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());

        log.info("Saving updated event to database with id {}", event.getId());

        return mapToResponse(eventRepository.save(event));
    }

    @Override
    public void deleteEvent(String eventId) {
        log.info("Deleting event with id {}", eventId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        eventRepository.delete(event);

        log.info("Event with id {} has been deleted successfully", eventId);
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
