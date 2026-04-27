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

/**
 * REST controller for managing Event entities and their related resources.
 *
 * Provides endpoints for creating, retrieving, updating, and canceling events,
 * as well as managing associated venues and schedule items.
 */
@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    /**
     * Constructs an EventController with the required service dependency.
     *
     * @param eventService service handling event operations
     */
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Request body for creating a new event.
     */
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

    /**
     * Request body for updating an existing event.
     *
     * All fields are optional and may be provided selectively.
     */
    public static class EventUpdateRequest {
        public String name;
        public String description;
        public LocalDateTime startDateTime;
        public LocalDateTime endDateTime;
    }

    /**
     * Creates a new event.
     *
     * Allowed Roles: ADMIN, ORGANIZER
     *
     * @param request request containing event name, description, start date/time,
     *                and end date/time
     * @return the created Event
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @PostMapping
    public Event createEvent(@Valid @RequestBody EventRequest request) {
        return eventService.createEvent(
            request.name,
            request.description,
            request.startDateTime,
            request.endDateTime
        );
    }

    /**
     * Retrieves all events in the system.
     *
     * Allowed Roles: Any authenticated user
     *
     * @return list of all events
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    /**
     * Retrieves a specific event by ID.
     *
     * Allowed Roles: Any authenticated user
     *
     * @param eventId event ID
     * @return the matching Event
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{eventId}")
    public Event getEventById(@PathVariable Long eventId) {
        return eventService.getEventById(eventId);
    }

    /**
     * Updates an existing event's details.
     *
     * Allowed Roles: ADMIN
     *
     * @param eventId event ID
     * @param request request containing updated event fields
     * @return the updated Event
     */
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

    /**
     * Cancels an event.
     *
     * Allowed Roles: ADMIN
     *
     * @param eventId event ID
     * @return the canceled Event
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{eventId}/cancel")
    public Event cancelEvent(@PathVariable Long eventId) {
        return eventService.cancelEvent(eventId);
    }

    /**
     * Adds a venue to an event.
     *
     * Allowed Roles: ADMIN
     *
     * @param eventId event ID
     * @param venueId venue ID
     * @return the updated Event
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{eventId}/venues/{venueId}")
    public Event addVenue(
        @PathVariable Long eventId,
        @PathVariable Long venueId
    ) {
        return eventService.addVenueToEvent(eventId, venueId);
    }

    /**
     * Removes a venue from an event.
     *
     * Allowed Roles: ADMIN
     *
     * @param eventId event ID
     * @param venueId venue ID
     * @return the updated Event
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{eventId}/venues/{venueId}")
    public Event removeVenue(
        @PathVariable Long eventId,
        @PathVariable Long venueId
    ) {
        return eventService.removeVenueFromEvent(eventId, venueId);
    }

    /**
     * Adds a schedule item to an event.
     *
     * Allowed Roles: ADMIN
     *
     * @param eventId event ID
     * @param item schedule item to add
     * @return the updated Event
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{eventId}/schedule")
    public Event addScheduleItem(
        @PathVariable Long eventId,
        @RequestBody ScheduleItem item
    ) {
        return eventService.addScheduleItem(eventId, item);
    }

    /**
     * Removes a schedule item from an event.
     *
     * Allowed Roles: ADMIN
     *
     * @param eventId event ID
     * @param scheduleItemId schedule item ID
     * @return the updated Event
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{eventId}/schedule/{scheduleItemId}")
    public Event removeScheduleItem(
        @PathVariable Long eventId,
        @PathVariable Long scheduleItemId
    ) {
        return eventService.removeScheduleItem(eventId, scheduleItemId);
    }

    /**
     * Retrieves all participants registered for an event.
     *
     * Allowed Roles: Any authenticated user
     *
     * @param eventId event ID
     * @return list of participants for the event
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{eventId}/participants")
    public List<Participant> listParticipants(@PathVariable Long eventId) {
        return eventService.listParticipants(eventId);
    }

    /**
     * Retrieves all vendors associated with an event.
     *
     * Allowed Roles: Any authenticated user
     *
     * @param eventId event ID
     * @return list of vendors for the event
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{eventId}/vendors")
    public List<Vendor> listVendors(@PathVariable Long eventId) {
        return eventService.listEventVendors(eventId);
    }

    /**
     * Retrieves all venues associated with an event.
     *
     * Allowed Roles: Any authenticated user
     *
     * @param eventId event ID
     * @return list of venues for the event
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{eventId}/venues")
    public List<Venue> listVenues(@PathVariable Long eventId) {
        return eventService.listEventVenues(eventId);
    }

    /**
     * Retrieves all schedule items associated with an event.
     *
     * Allowed Roles: Any authenticated user
     *
     * @param eventId event ID
     * @return list of schedule items for the event
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{eventId}/scheduleitems")
    public List<ScheduleItem> listScheduleItems(@PathVariable Long eventId) {
        return eventService.listEventScheduledItems(eventId);
    }
}