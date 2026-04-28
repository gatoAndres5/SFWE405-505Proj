package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entity.Event;
import com.example.demo.entity.EventStatus;
import com.example.demo.entity.Participant;
import com.example.demo.entity.Registration;
import com.example.demo.entity.ScheduleItem;
import com.example.demo.entity.Vendor;
import com.example.demo.entity.Venue;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.ScheduleItemRepository;
import com.example.demo.repository.VenueRepository;
import com.example.demo.entity.User;
import com.example.demo.entity.EventAssignment;
import com.example.demo.entity.EventAssignment.EventAssignmentRole;
import com.example.demo.entity.UserRole;
/**
 * Service class for managing events and their related entities.
 * Handles business logic for creating, updating, cancelling events,
 * and managing venues, schedule items, participants, and vendors 
 * associated with events.
 */
@Service
@Transactional
public class EventService {

    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    private final ScheduleItemRepository scheduleItemRepository;
    
    /**
     * Constructs event service with nececssary repositories.
     * 
     * @param eventRepository Repository for event data
     * @param venueRepository Repository for venue data
     * @param scheduleItemRepository Repository for schedule item data
     */
    public EventService(
        EventRepository eventRepository,
        VenueRepository venueRepository,
        ScheduleItemRepository scheduleItemRepository
    ) {
        this.eventRepository = eventRepository;
        this.venueRepository = venueRepository;
        this.scheduleItemRepository = scheduleItemRepository;
    }

    /**
     * Retrieves an event by its ID.
     * 
     * @param eventId ID of the event
     * @return Return event
     * @throws ResponseStatusException if event is not found
     */
    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Event not found, id: " + eventId
            ));
    }

    /**
     * Retrieves a list of all events.
     * @return Return ist of all events
     */

    /*public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }*/

    /**
     * Creates a new event
     * The event is a draft by default
     * If the user creating the event is an organizer, they are automatically set as the event organizer
     * @param user User creating the event
     * @param name Name of event
     * @param description Description of event
     * @param startDateTime Start date and time of event
     * @param endDateTime End date and time of event
     * @return Return created event
     * @throws IllegalArgumentException if required fields are missing or dates are invalid
     */
    public Event createEvent(
        User user,
        String name,
        String description,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
    ) {
        validateRequiredFields(name, description, startDateTime, endDateTime);
        validateDateRange(startDateTime, endDateTime);

        Event event = new Event(name.trim(), description.trim(), startDateTime, endDateTime);
        event.setStatus(EventStatus.DRAFT);
        event.setCreationTime(LocalDateTime.now());
        event.setUpdateTime(LocalDateTime.now());

        if (user.getRole() == UserRole.ORGANIZER) {
            EventAssignment assignment = new EventAssignment(
                event,
                user,
                EventAssignmentRole.ORGANIZER
            );
            event.getAssignments().add(assignment);
        }

        return eventRepository.save(event);
    }
    /**
     * Checks whether a user is an organizer for an event or not
     * @param event Event that is being checked
     * @param user User that is being checked
     * @return Return boolean true or false
     */
    private boolean isOrganizer(Event event, User user) {
    return event.getAssignments().stream()
        .anyMatch(a ->
            a.getUser().getId().equals(user.getId()) &&
            a.getRole() == EventAssignmentRole.ORGANIZER &&
            a.isActive()
        );
    }
    /**
     * Checks whether a user is staff for an event or not
     * @param event Event that is being checked
     * @param user User that is being checked
     * @return Return boolean true or false
     */
    private boolean isStaff(Event event, User user) {
        return event.getAssignments().stream()
            .anyMatch(a ->
                a.getUser().getId().equals(user.getId()) &&
                a.getRole() == EventAssignmentRole.STAFF &&
                a.isActive()
            );
    }
    /**
     * Checks whether a user is an admin or not
     * @param user User that is being checked
     * @return
     */
    private boolean isAdmin(User user) {
    return user.getRole() == UserRole.ADMIN;
    }
    /**
     * Updates event.
     * Only admin or organizer can who are assigned to the event can update it
     * @param user User updating event
     * @param eventId ID of event
     * @param name Name of event
     * @param description Description of event
     * @param startDateTime Start date and time of event
     * @param endDateTime End date and time of event
     * @return Return updated event
     * @throws IllegalArgumentException if updated fields are invalid
     */
    public Event updateEventDetails(
        Long eventId,
        User user,
        String name,
        String description,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
    ) {
        Event event = getEventById(eventId);

        if (!isAdmin(user) && !isOrganizer(event, user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        String updatedName = event.getName();
        String updatedDescription = event.getDescription();
        LocalDateTime updatedStart = event.getStartDateTime();
        LocalDateTime updatedEnd = event.getEndDateTime();

        if (name != null && !name.trim().isEmpty()) {
            updatedName = name.trim();
        }

        if (description != null && !description.trim().isEmpty()) {
            updatedDescription = description.trim();
        }

        if (startDateTime != null) {
            updatedStart = startDateTime;
        }

        if (endDateTime != null) {
            updatedEnd = endDateTime;
        }

        validateRequiredFields(updatedName, updatedDescription, updatedStart, updatedEnd);
        validateDateRange(updatedStart, updatedEnd);

        event.setName(updatedName);
        event.setDescription(updatedDescription);
        event.setStartDateTime(updatedStart);
        event.setEndDateTime(updatedEnd);
        event.setUpdateTime(LocalDateTime.now());

        return eventRepository.save(event);
    }

    /**
     * Cancels an event by setting its status to CANCELLED.
     * @param user User who is cancelling event.
     * @param eventId ID of event
     * @return Return canceled event
     */
    public Event cancelEvent(Long eventId, User user) {
        Event event = getEventById(eventId);

        if (!isAdmin(user) && !isOrganizer(event, user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        event.setStatus(EventStatus.CANCELLED);
        event.setUpdateTime(LocalDateTime.now());
        return eventRepository.save(event);
    }
    /**
     * Activates an event, but only if the user requesting the activation is the admin or organizer
     * @param eventId ID of event
     * @param user User that is requesting the event activation
     * @return Return activated event
     */
    public Event activateEvent(Long eventId, User user) {
        Event event = getEventById(eventId);

        if (!isAdmin(user) && !isOrganizer(event, user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        event.setStatus(EventStatus.ACTIVE);
        event.setUpdateTime(LocalDateTime.now());

        return eventRepository.save(event);
    }
    /**
     * Assigns user to an event with a specific role.
     * Administrators can assign organizers and staff
     * Organizers can assign staff to their events
     * Only admin can assign organizers
     * @param eventId ID of event
     * @param assignedUser User that is being assigned to an event with a specific role
     * @param currentUser User who is assigning a role
     * @param role Role that will be assigned
     * @return Return updated event with the user and their role assigned 
     */
        public Event assignUserToEvent(
        Long eventId,
        User assignedUser,
        User currentUser,
        EventAssignmentRole role
    ) {
        Event event = getEventById(eventId);

        if (!isAdmin(currentUser) && !isOrganizer(event, currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        if (role == EventAssignmentRole.ORGANIZER && !isAdmin(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        boolean alreadyAssigned = event.getAssignments().stream()
            .anyMatch(a ->
                a.getUser().getId().equals(assignedUser.getId()) &&
                a.getRole() == role &&
                a.isActive()
            );

        if (!alreadyAssigned) {
            EventAssignment assignment = new EventAssignment(event, assignedUser, role);
            event.getAssignments().add(assignment);
        }

        event.setUpdateTime(LocalDateTime.now());
        return eventRepository.save(event);
    }

    /**
     * Adds venue to an event.
     * 
     * @param eventId ID of event
     * @param venueId ID of venue
     * @return Return updated event
     * @throws IllegalArgumentException if the venue is not found
     */
    public Event addVenueToEvent(Long eventId, Long venueId) {
        Event event = getEventById(eventId);
        Venue venue = venueRepository.findById(venueId)
            .orElseThrow(() -> new IllegalArgumentException("Venue not found, id: " + venueId));

        event.addVenue(venue);
        event.setUpdateTime(LocalDateTime.now());
        return eventRepository.save(event);
    }

    /**
     * Removes venue from event.
     * @param eventId ID of event
     * @param venueId ID of venue
     * @return Returns updated event
     * @throws IllegalArgumentException if the venue is not found or it is not assigned to the event
     */
    public Event removeVenueFromEvent(Long eventId, Long venueId) {
        Event event = getEventById(eventId);
        Venue venue = venueRepository.findById(venueId)
            .orElseThrow(() -> new IllegalArgumentException("Venue not found, id: " + venueId));

        if (!event.getVenues().contains(venue)) {
            throw new IllegalArgumentException("Venue is not assigned to this event");
        }

        event.removeVenue(venue);
        event.setUpdateTime(LocalDateTime.now());
        return eventRepository.save(event);
    }

    /**
     * Adds schedule item to event.
     * 
     * @param eventId ID of event
     * @param scheduleItem ID of schedule item
     * @return Return updated event
     * @throws IllegalArgumentException if the schedule is null
     */
    public Event addScheduleItem(Long eventId, User user, ScheduleItem scheduleItem) {
        Event event = getEventById(eventId);

        if (!isAdmin(user) && !isOrganizer(event, user) && !isStaff(event, user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        if (scheduleItem == null) {
            throw new IllegalArgumentException("Schedule item is required");
        }

        scheduleItem.setEvent(event);
        event.getScheduledItems().add(scheduleItem);
        event.setUpdateTime(LocalDateTime.now());

        return eventRepository.save(event);
    }

    /**
     * Removes a schedule item from event.
     * 
     * @param eventId
     * @param scheduleItemId
     * @return Return updated event
     * @throws IllegalArgumentException if the schedule is null or it is not assigned to event
     */
    public Event removeScheduleItem(Long eventId, Long scheduleItemId) {
        Event event = getEventById(eventId);
        ScheduleItem scheduleItem = scheduleItemRepository.findById(scheduleItemId)
            .orElseThrow(() -> new IllegalArgumentException("Schedule item not found, id: " + scheduleItemId));

        if (scheduleItem.getEvent() == null || !scheduleItem.getEvent().getId().equals(eventId)) {
            throw new IllegalArgumentException("Schedule item does not belong to this event");
        }

        event.getScheduledItems().remove(scheduleItem);
        scheduleItem.setEvent(null);
        event.setUpdateTime(LocalDateTime.now());

        return eventRepository.save(event);
    }
    /**
     * Retrieves list of all venues for event with  ID eventId.
     * @param eventId ID of event
     * @return Return list of all venues for event
     */
    public List<Venue> listEventVenues(Long eventId) {
        return getEventById(eventId).getVenues();
    }

    /**
     * Retrieves list of all vendors for event with ID eventId.
     * @param eventId ID of event
     * @return Return list of all vendors for event
     */
    public List<Vendor> listEventVendors(Long eventId) {
        return getEventById(eventId).getVendors();
    }

    /**
     * Retrieves list of all schedule items for event with ID eventId.
     * @param eventId ID of event
     * @return Return list of schedule items in event
     */
    public List<ScheduleItem> listEventScheduledItems(Long eventId) {
        return getEventById(eventId).getScheduledItems();
    }

    /**
     * Retrieves list of all participants for event with ID eventId.
     * @param eventId ID of event
     * @return List of participants registered for the event
     */
    public List<Participant> listParticipants(Long eventId) {
        Event event = getEventById(eventId);

        List<Participant> participants = new ArrayList<>();
        for (Registration registration : event.getRegistrations()) {
            if (registration.getParticipant() != null) {
                participants.add(registration.getParticipant());
            }
        }

        return participants;
    }

    /**
     * Validates that all required fields for event are present.
     * 
     * @param name Name of event
     * @param description Description of event
     * @param startDateTime Start date and time of event
     * @param endDateTime End date and time of event
     * @throws IllegalArgumentException if any of the fields is missing
     */
    private void validateRequiredFields(
        String name,
        String description,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
    ) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Event name is required");
        }

        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Event description is required");
        }

        if (startDateTime == null) {
            throw new IllegalArgumentException("Start date/time is required");
        }

        if (endDateTime == null) {
            throw new IllegalArgumentException("End date/time is required");
        }
    }
    
    /**
     * Validates that the end time is after the start time
     * 
     * @param startDateTime Start date and time of event
     * @param endDateTime End date and time of event
     * @throws IllegalArgumentException if the start time is after the end time
     */
    private void validateDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (!endDateTime.isAfter(startDateTime)) {
            throw new IllegalArgumentException("End date/time must be after start date/time");
        }
    }
    /**
     * Returns all events for admin, and only assigned events for other types of user 
     * @param user The user requesting the event
     * @return Return list of events
     */
    public List<Event> getEventsForUser(User user) {
        if (isAdmin(user)) {
            return eventRepository.findAll();
        }

        return eventRepository.findAll().stream()
            .filter(event ->
                event.getAssignments().stream().anyMatch(a ->
                    a.getUser().getId().equals(user.getId()) &&
                    a.isActive()
                )
            )
            .toList();
    }

}
