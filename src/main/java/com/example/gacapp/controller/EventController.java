package com.example.gacapp.controller;

import com.example.gacapp.dto.request.EventRequest;
import com.example.gacapp.dto.response.ApiResponse;
import com.example.gacapp.dto.response.EventResponse;
import com.example.gacapp.service.EventService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/event")
@RequiredArgsConstructor
@Tag(
        name = "Events",
        description = "APIs for managing events created by Admin"
)
public class EventController {

    private final EventService eventService;
    private final ObjectMapper objectMapper;

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create Events by the Admin",
            description = "This allows the Admin to create new events."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Message sent successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorised"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<EventResponse>> createEvent(
            @RequestPart("event") String eventJson,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws JsonProcessingException {

        EventRequest request =
                objectMapper.readValue(eventJson, EventRequest.class);

        EventResponse response =
                eventService.createEvent(request, file);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Event successfully created")
        );
    }

    @GetMapping
    @Operation(
            summary = "Retrieve all Events",
            description = "This allows all events to be retrieved by both the Admin and Leaders."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Message sent successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<Page<EventResponse>>> getEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        Page<EventResponse> response =
                eventService.getEvents(pageable);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Events retrieved successfully")
        );
    }

    @GetMapping("/{eventId}")
    @Operation(
            summary = "Get Event by an Id ",
            description = "This allows the Admin to retrieve an event by an Id."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Message sent successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<EventResponse>> getEvent(
            @PathVariable String eventId
    ) {

        EventResponse response =
                eventService.getEvent(eventId);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Event retrieved successfully")
        );
    }

    @PatchMapping(
            value = "/{eventId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update an Event using an Id",
            description = "This also allows an Admin to update an event using the Id."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Message sent successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorised"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<EventResponse>> updateEvent(
            @PathVariable String eventId,
            @RequestPart("event") String eventJson,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws JsonProcessingException {

        EventRequest request =
                objectMapper.readValue(eventJson, EventRequest.class);

        EventResponse response =
                eventService.updateEvent(eventId, request, file);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Event updated successfully")
        );
    }

    @DeleteMapping("/delete-event/{eventId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete an Event using the Id",
            description = "This allows the Admin to delete an event using an Id."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Message sent successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorised"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<Void>> deleteEvent(
            @PathVariable String eventId
    ) {



        eventService.deleteEvent(eventId);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Event deleted successfully")
        );
    }
}