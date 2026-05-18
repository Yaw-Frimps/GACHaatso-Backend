package com.example.gacapp.controller;

import com.example.gacapp.dto.request.EventRequest;
import com.example.gacapp.dto.response.ApiResponse;
import com.example.gacapp.dto.response.EventResponse;
import com.example.gacapp.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/v1/event")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping("/create-event")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EventResponse>> createEvent(@RequestBody EventRequest request){
        EventResponse response = eventService.createEvent(request);
        return ResponseEntity.ok(ApiResponse.success(response,"Event successfully created"));
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<Page<EventResponse>>> getAllEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        Page<EventResponse> response = eventService.getAllEvent(pageable);
        return ResponseEntity.ok(ApiResponse.success(response,"Events retrieved successfully"));
    }

    @PostMapping("/upload-image")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> uploadEventImage(@RequestParam("file") MultipartFile file) {
        String imageUrl = eventService.uploadImage(file);
        return ResponseEntity.ok(ApiResponse.success(imageUrl, "Image uploaded successfully"));
    }

    @PostMapping("/{eventId}")
    public ResponseEntity<ApiResponse<EventResponse>> getEventById(@PathVariable String eventId){
        EventResponse response = eventService.getEventById(eventId);
        return ResponseEntity.ok(ApiResponse.success(response,"Event retrieved successfully"));
    }

    @PatchMapping("/update-event/{eventId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EventResponse>> updateEvent(@PathVariable String eventId, @RequestBody EventRequest request){
        EventResponse response = eventService.updateEvent(eventId, request);
        return ResponseEntity.ok(ApiResponse.success(response,"Event updated successfully"));
    }

    @DeleteMapping("/delete-event/{eventId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(@PathVariable String eventId){
        eventService.deleteEvent(eventId);
        return ResponseEntity.ok(ApiResponse.success(null,"Event deleted successfully"));
    }

}
