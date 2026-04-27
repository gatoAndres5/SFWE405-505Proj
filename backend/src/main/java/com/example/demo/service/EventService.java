package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entity.Event;
import com.example.demo.entity.EventStatus;
import com.example.demo.entity.Participant;
import com.example.demo.entity.Registration;
import com.example.demo.entity.ScheduleItem;
import com.example.demo.entity.Vendor;
import com.example.demo.entity.Venue;
import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import com.example.demo.entity.EventAssignment.EventAssignmentRole;
import com.example.demo.entity.EventAssignment;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.ScheduleItemRepository;
import com.example.demo.repository.VenueRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.EventAssignmentRepository;

@Service
@Transactional
public class EventService {

    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    private final ScheduleItemRepository scheduleItemRepository;
    private final EventAssignmentRepository eventAssignmentRepository;
    private final UserRepository userRepository;

    public EventService(
        EventRepository eventRepository,
        VenueRepository venueRepository,
        ScheduleItemRepository scheduleItemRepository,
        UserRepository userRepository,
        EventAssignmentRepository eventAssignmentRepository
    ) {
        this.eventRepository = eventRepository;
        this.venueRepository = venueRepository;
        this.scheduleItemRepository = scheduleItemRepository;
        this.userRepository = userRepository;
        this.eventAssignmentRepository = eventAssignmentRepository;
    }

    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Event not found, id: " + eventId
            ));
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event createEvent(
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

        Event savedEvent = eventRepository.save(event);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User currentUser = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Authenticated user not found: " + username
            ));

        if (currentUser.getRole() == UserRole.ORGANIZER) {
            EventAssignment assignment = new EventAssignment(
                savedEvent,
                currentUser,
                EventAssignmentRole.ORGANIZER
            );
            eventAssignmentRepository.save(assignment);
        }

        return savedEvent;
    }

    public Event updateEventDetails(
        Long eventId,
        String name,
        String description,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
    ) {
        Event event = getEventById(eventId);

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

    public Event cancelEvent(Long eventId) {
        Event event = getEventById(eventId);
        event.setStatus(EventStatus.CANCELLED);
        event.setUpdateTime(LocalDateTime.now());
        return eventRepository.save(event);
    }

    public Event addVenueToEvent(Long eventId, Long venueId) {
        Event event = getEventById(eventId);
        Venue venue = venueRepository.findById(venueId)
            .orElseThrow(() -> new IllegalArgumentException("Venue not found, id: " + venueId));

        event.addVenue(venue);
        event.setUpdateTime(LocalDateTime.now());
        return eventRepository.save(event);
    }

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

    public Event addScheduleItem(Long eventId, ScheduleItem scheduleItem) {
        Event event = getEventById(eventId);

        if (scheduleItem == null) {
            throw new IllegalArgumentException("Schedule item is required");
        }

        scheduleItem.setEvent(event);
        event.getScheduledItems().add(scheduleItem);
        event.setUpdateTime(LocalDateTime.now());

        return eventRepository.save(event);
    }

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

    public List<Venue> listEventVenues(Long eventId) {
        return getEventById(eventId).getVenues();
    }

    public List<Vendor> listEventVendors(Long eventId) {
        return getEventById(eventId).getVendors();
    }

    public List<ScheduleItem> listEventScheduledItems(Long eventId) {
        return getEventById(eventId).getScheduledItems();
    }

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

    private void validateDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (!endDateTime.isAfter(startDateTime)) {
            throw new IllegalArgumentException("End date/time must be after start date/time");
        }
    }
}