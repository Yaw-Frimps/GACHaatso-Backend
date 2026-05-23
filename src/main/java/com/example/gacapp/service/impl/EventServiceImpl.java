package com.example.gacapp.service.impl;

import com.example.gacapp.dto.request.EventRequest;
import com.example.gacapp.dto.response.EventResponse;
import com.example.gacapp.exception.EventNotFoundException;
import com.example.gacapp.model.Event;
import com.example.gacapp.repository.EventRepository;
import com.example.gacapp.service.EventService;
import com.example.gacapp.util.FileStorageServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final FileStorageServiceImpl fileStorageService;

    private static final String EVENT = "events";
    private static final String EVENT_NOT_FOUND_WITH_ID = "Event not found with id: ";

    @Transactional
    @Override
    @CacheEvict(value = "events", allEntries = true)
    public EventResponse createEvent(EventRequest request, MultipartFile file) {

        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .date(request.getDate())
                .build();

        Event savedEvent = eventRepository.save(event);

        if (file != null && !file.isEmpty()) {

            String storedFileName = fileStorageService.storeFile(
                    file,
                    EVENT,
                    savedEvent.getId(),
                    null
            );

            String imageUrl = fileStorageService.getFileUrl(
                    EVENT,
                    savedEvent.getId(),
                    storedFileName
            );

//            String imagePath = "/uploads/" + EVENT + "/" + savedEvent.getId() + "/" + storedFileName;

//            savedEvent.setImageUrl(imagePath);

            savedEvent.setImageUrl(imageUrl);

            savedEvent = eventRepository.save(savedEvent);
        }

        return mapToResponse(savedEvent);
    }

    @Transactional
    @Override
    @CacheEvict(value = "events", allEntries = true)
    public EventResponse updateEvent(
            String eventId,
            EventRequest request,
            MultipartFile file
    ) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new EventNotFoundException(EVENT_NOT_FOUND_WITH_ID + eventId)
                );

        if (request.getTitle() != null) event.setTitle(request.getTitle());
        if (request.getDescription() != null) event.setDescription(request.getDescription());
        if (request.getLocation() != null) event.setLocation(request.getLocation());
        if (request.getDate() != null) event.setDate(request.getDate());

        if (file != null && !file.isEmpty()) {

            // ✅ FIX: extract filename from URL safely
            String oldFileName = null;

            if (event.getImageUrl() != null) {
                oldFileName = event.getImageUrl()
                        .substring(event.getImageUrl().lastIndexOf("/") + 1);
            }

            String storedFileName = fileStorageService.storeFile(
                    file,
                    EVENT,
                    event.getId(),
                    oldFileName
            );

            String imageUrl = fileStorageService.getFileUrl(
                    EVENT,
                    event.getId(),
                    storedFileName
            );

            event.setImageUrl(imageUrl);
        }

        Event updatedEvent = eventRepository.save(event);

        return mapToResponse(updatedEvent);
    }

    @Override
    @Cacheable(value = "events", key = "#eventId")
    public EventResponse getEventById(String eventId) {

        return eventRepository.findById(eventId)
                .map(this::mapToResponse)
                .orElseThrow(() ->
                        new EventNotFoundException(EVENT_NOT_FOUND_WITH_ID + eventId)
                );
    }

    @Override
    @Cacheable(value = "events",
            key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort")
    public Page<EventResponse> getAllEvent(Pageable pageable) {
        return eventRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Transactional
    @Override
    @CacheEvict(value = "events", allEntries = true)
    public void deleteEvent(String eventId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new EventNotFoundException(EVENT_NOT_FOUND_WITH_ID + eventId)
                );

        eventRepository.delete(event);
    }

    private EventResponse mapToResponse(Event event) {
        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .location(event.getLocation())
                .date(event.getDate())
                .imageUrl(event.getImageUrl())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }
}