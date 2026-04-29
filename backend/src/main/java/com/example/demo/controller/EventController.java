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
import com.example.demo.entity.UserRole;
import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entity.User;
import com.example.demo.entity.EventAssignment.EventAssignmentRole;
import com.example.demo.repository.UserRepository;
/**
 * REST controller to manage events.
 * Provides endpoints for creating, retrieving, 
 * updating and managing event-related resources 
 * like venues, vendors, participants, and 
 * schedule items.
 */
@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;
    private final UserRepository userRepository;

    /**
     * Constructs an EventController with the given service.
     * @param eventService Service used to handle event operations.
     */
    public EventController(EventService eventService, UserRepository userRepository) {
        this.eventService = eventService;
        this.userRepository = userRepository;
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
     * Retrieves the current user from the security context.
     * @param principal The security principal representing the authenticated user
     * @return Return the authenticated user object.
     * @throws ResponseStatusException if the user is not authenticated or it cannot be found
     */
    private User getCurrentUser(Principal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
        }

        return userRepository.findByUsername(principal.getName())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Authenticated user not found"
            ));
    }
    /**
     * Request body for updating an existing event.
     * All fields are optional.
     */
    public static class EventUpdateRequest {
        public String name;
        public String description;
        public LocalDateTime startDateTime;
        public LocalDateTime endDateTime;
    }
    /**
     * Creates a new event.
     * Accesible to admin role or event organizer.
     * 
     * @param principal The security principal representing the authenticated user
     * @param request Request the event details
     * @return Return event.
     * @throws ResponseStatusException if the user is not authenticated or it cannot be found
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @PostMapping
    public Event createEvent(
        Principal principal,
        @Valid @RequestBody EventRequest request
    ) {
        User currentUser = getCurrentUser(principal);

        return eventService.createEvent(
            currentUser,
            request.name,
            request.description,
            request.startDateTime,
            request.endDateTime
        );
    }
    /**
     * Retrieves a list of events accesible to the authenticated user.
     * @param principal The security principal representing the authenticated user
     * @return Return list of events the user is allowed to view.
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public List<Event> getAllEvents(Principal principal) {
        User currentUser = getCurrentUser(principal);
        return eventService.getEventsForUser(currentUser);
    }

    /**
     * Retrieves an event by its ID.
     * 
     * @param eventId ID of an event
     * @return  Return the requested event
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{eventId}")
    public Event getEventById(@PathVariable Long eventId) {
        return eventService.getEventById(eventId);
    }

    /**
     * Updates an existing event if the user is admin or an event organizer.
     * @param principal The security principal representing the authenticated user
     * @param eventId ID of event
     * @param request Updated event data
     * @return Return the updated event
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @PutMapping("/{eventId}")
    public Event updateEvent(
        Principal principal,
        @PathVariable Long eventId,
        @RequestBody EventUpdateRequest request
    ) {
        User currentUser = getCurrentUser(principal);

        return eventService.updateEventDetails(
            eventId,
            currentUser,
            request.name,
            request.description,
            request.startDateTime,
            request.endDateTime
        );
    }

    /**
     * Cancels an event if the authenticated user is an admin or an event organizer.
     * @param principal The security principal representing the authenticated user
     * @param eventId ID of event
     * @return Return the cancelled event
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @PutMapping("/{eventId}/cancel")
    public Event cancelEvent(
        Principal principal,
        @PathVariable Long eventId
    ) {
        User currentUser = getCurrentUser(principal);
        return eventService.cancelEvent(eventId, currentUser);
    }
    
    /**
     * Activates event if the request is made by an admin or an event organizer
     * @param principal The security principal representing the authenticated user
     * @param eventId ID of event
     * @return Return activated event.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @PutMapping("/{eventId}/activate")
    public Event activateEvent(
        Principal principal,
        @PathVariable Long eventId
    ) {
        User currentUser = getCurrentUser(principal);
        return eventService.activateEvent(eventId, currentUser);
    }
    /**
     * Adds a venue to event.
     * 
     * @param eventId ID of event
     * @param venueId ID of venue
     * @return Return updated event
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
     * Removes a venue from event.
     * @param eventId ID of event
     * @param venueId ID of venue
     * @return Return updated event
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
      * Adds a schedule item to event if the authenitcated user is admin or event organizer or event staff
      * @param principal The security principal representing the authenticated user
      * @param eventId ID of event
      * @param item Item to add
      * @return Return updated event
      */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER', 'STAFF')")
    @PostMapping("/{eventId}/schedule")
    public Event addScheduleItem(
        Principal principal,
        @PathVariable Long eventId,
        @RequestBody ScheduleItem item
    ) {
        User currentUser = getCurrentUser(principal);
        return eventService.addScheduleItem(eventId, currentUser, item);
    }
    /**
     * Remove a schedule item in event
     * 
     * @param eventId ID of event
     * @param scheduleItemId Item to remove
     * @return Return updated event
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
     * Retrieves all participants from an event
     * 
     * @param eventId ID of event
     * @return Return list of all participants in event with eventId
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{eventId}/participants")
    public List<Participant> listParticipants(@PathVariable Long eventId) {
        return eventService.listParticipants(eventId);
    }

    /**
     * Retrieves all vendors for an event.
     * 
     * @param eventId ID of event
     * @return Return list of all vendors in event with eventId 
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{eventId}/vendors")
    public List<Vendor> listVendors(@PathVariable Long eventId) {
        return eventService.listEventVendors(eventId);
    }

    /**
     * Retrieves all venues for event.
     * 
     * @param eventId ID of event
     * @return Return list of all venues for event with eventId
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{eventId}/venues")
    public List<Venue> listVenues(@PathVariable Long eventId) {
        return eventService.listEventVenues(eventId);
    }

    /**
     * Retrieves all schedule items for event.
     * @param eventId ID of event
     * @return Return list of all schedule items for event with eventId
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{eventId}/scheduleitems")
    public List<ScheduleItem> listScheduleItems(@PathVariable Long eventId) {
        return eventService.listEventScheduledItems(eventId);
    }

    /**
     * Allows admin to assign an organizer with their userID.
     * @param principal The security principal representing the authenticated user
     * @param eventId ID of event to which an organizer will be assigned.
     * @param userId User ID of the organizer.
     * @return Return event with new organizer.
     * @throws ResponseStatusException if the user is not found.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{eventId}/assign/organizer/{userId}")
    public Event assignOrganizer(
        Principal principal,
        @PathVariable Long eventId,
        @PathVariable Long userId
    ) {
        User currentUser = getCurrentUser(principal);

        User assignedUser = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "User not found, id: " + userId
            ));

        return eventService.assignUserToEvent(
            eventId,
            assignedUser,
            currentUser,
            EventAssignmentRole.ORGANIZER
        );
    }

    /**
     * Allows admin or event organizer to assign staff to an event. 
     * @param principal The security principal representing the authenticated user
     * @param eventId ID of event to which staff will be added to
     * @param userId User ID of the staff that will be added to an event.
     * @return Return event with new staff
     * @throws ResponseStatusException if the user is not found.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @PostMapping("/{eventId}/assign/staff/{userId}")
    public Event assignStaff(
        Principal principal,
        @PathVariable Long eventId,
        @PathVariable Long userId
    ) {
        User currentUser = getCurrentUser(principal);

        User assignedUser = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "User not found, id: " + userId
            ));

        return eventService.assignUserToEvent(
            eventId,
            assignedUser,
            currentUser,
            EventAssignmentRole.STAFF
        );
    }

    /**
     * DTO representing a simplified user.
     * Used for frontend dropdown menu.
     */
    public static class UserOption {
        public Long id;
        public String username;

        public UserOption(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
        }
    }
    
    /**
     * Retrives a list of users with the organizer role
     * Used for frontend dropdown menu.
     * @return Returns list of organizers
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/organizers")
    public List<UserOption> getOrganizers() {
        return userRepository.findAll().stream()
            .filter(user -> user.getRole() == UserRole.ORGANIZER)
            .map(UserOption::new)
            .toList();
    }
    /**
     * Retrieves a list of users with the staff role
     * Used for frontend dropdown menu
     * @return Returns a list of staff
     */
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @GetMapping("/staff")
    public List<UserOption> getStaff() {
        return userRepository.findAll().stream()
            .filter(user -> user.getRole() == UserRole.STAFF)
            .map(UserOption::new)
            .toList();
    }
}