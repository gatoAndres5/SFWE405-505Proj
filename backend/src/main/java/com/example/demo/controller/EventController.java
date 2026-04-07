package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Event;
import com.example.demo.entity.Participant;
import com.example.demo.entity.ScheduleItem;
import com.example.demo.entity.Vendor;
import com.example.demo.entity.Venue;
import com.example.demo.service.EventService;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    public static class EventRequest {
        @NotBlank
        public String name;

        @NotBlank
        public String description;

        @NotNull
        public LocalDateTime startDateTime;

        @NotNull
        public LocalDateTime endDateTime;
    }

    public static class EventUpdateRequest {
        public String name;
        public String description;
        public LocalDateTime startDateTime;
        public LocalDateTime endDateTime;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Event createEvent(@Valid @RequestBody EventRequest request) {
        return eventService.createEvent(
            request.name,
            request.description,
            request.startDateTime,
            request.endDateTime
        );
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{eventId}")
    public Event getEventById(@PathVariable Long eventId) {
        return eventService.getEventById(eventId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{eventId}")
    public Event updateEvent(
        @PathVariable Long eventId,
        @RequestBody EventUpdateRequest request
    ) {
        return eventService.updateEventDetails(
            eventId,
            request.name,
            request.description,
            request.startDateTime,
            request.endDateTime
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{eventId}/cancel")
    public Event cancelEvent(@PathVariable Long eventId) {
        return eventService.cancelEvent(eventId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{eventId}/venues/{venueId}")
    public Event addVenue(
        @PathVariable Long eventId,
        @PathVariable Long venueId
    ) {
        return eventService.addVenueToEvent(eventId, venueId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{eventId}/venues/{venueId}")
    public Event removeVenue(
        @PathVariable Long eventId,
        @PathVariable Long venueId
    ) {
        return eventService.removeVenueFromEvent(eventId, venueId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{eventId}/schedule")
    public Event addScheduleItem(
        @PathVariable Long eventId,
        @RequestBody ScheduleItem item
    ) {
        return eventService.addScheduleItem(eventId, item);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{eventId}/schedule/{scheduleItemId}")
    public Event removeScheduleItem(
        @PathVariable Long eventId,
        @PathVariable Long scheduleItemId
    ) {
        return eventService.removeScheduleItem(eventId, scheduleItemId);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{eventId}/participants")
    public List<Participant> listParticipants(@PathVariable Long eventId) {
        return eventService.listParticipants(eventId);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{eventId}/vendors")
    public List<Vendor> listVendors(@PathVariable Long eventId) {
        return eventService.listEventVendors(eventId);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{eventId}/venues")
    public List<Venue> listVenues(@PathVariable Long eventId) {
        return eventService.listEventVenues(eventId);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{eventId}/scheduleitems")
    public List<ScheduleItem> listScheduleItems(@PathVariable Long eventId) {
        return eventService.listEventScheduledItems(eventId);
    }
}