package com.example.demo.service;

import com.example.demo.entity.Event;
import com.example.demo.entity.EventAssignment;
import com.example.demo.entity.ScheduleItem;
import com.example.demo.entity.User;
import com.example.demo.entity.Venue;
import com.example.demo.repository.EventAssignmentRepository;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.ScheduleItemRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.VenueRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleItemService {

    private final ScheduleItemRepository scheduleItemRepository;
    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    private final UserRepository userRepository;
    private final EventAssignmentRepository eventAssignmentRepository;

    public ScheduleItemService(ScheduleItemRepository scheduleItemRepository,
                              EventRepository eventRepository,
                              VenueRepository venueRepository,
                              UserRepository userRepository,
                              EventAssignmentRepository eventAssignmentRepository) {
        this.scheduleItemRepository = scheduleItemRepository;
        this.eventRepository = eventRepository;
        this.venueRepository = venueRepository;
        this.userRepository = userRepository;
        this.eventAssignmentRepository = eventAssignmentRepository;
    }

    @Transactional(readOnly = true)
    public List<ScheduleItem> getAllScheduleItems() {
        return scheduleItemRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<ScheduleItem> getMyScheduleItems(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        switch (user.getRole()) {
            case ADMIN:
                return scheduleItemRepository.findAll();

            case ORGANIZER:
            case STAFF: {
                List<Long> eventIds = eventAssignmentRepository.findByUser_Id(user.getId())
                        .stream()
                        .filter(EventAssignment::isActive)
                        .map(a -> a.getEvent().getId())
                        .collect(Collectors.toList());

                System.out.println("Event IDs: " + eventIds);

                List<ScheduleItem> items = scheduleItemRepository.findByEvent_IdIn(eventIds);
                System.out.println("Schedule items found: " + items.size());

                return items;
            }

            case PARTICIPANT:
                return List.of();

            default:
                return List.of();
        }
    }

    @Transactional(readOnly = true)
    public ScheduleItem getScheduleItemById(Long id) {
        return scheduleItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Schedule Item not found."));
    }
    
    // BUSINESS METHODS
    @Transactional
    public ScheduleItem createScheduleItem(Long eventId, Long venueId, 
                                           String title, String description,
                                           LocalDateTime startDateTime, 
                                           LocalDateTime endDateTime, String type) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Event not found."));
        
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Venue not found."));

        validateTimeRange(startDateTime, endDateTime);

        ScheduleItem scheduleItem = new ScheduleItem(
                event, 
                venue, 
                title, 
                description, 
                startDateTime, 
                endDateTime, 
                type
        );
        return scheduleItemRepository.save(scheduleItem);
    }

    @Transactional
    public ScheduleItem updateScheduleItem(Long id, Long eventId, 
                                           Long venueId, String title,
                                           String description, LocalDateTime startDateTime, 
                                           LocalDateTime endDateTime, String type) {

        ScheduleItem item = getScheduleItemById(id);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Event not found."));

        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Venue not found."));

        validateTimeRange(startDateTime, endDateTime);

        // Updating fields
        item.setEvent(event);
        item.setVenue(venue);
        item.setTitle(title);
        item.setDescription(description);
        item.setStartDateTime(startDateTime);
        item.setEndDateTime(endDateTime);
        item.setType(type);
        item.setUpdatedAt(LocalDateTime.now());

        return scheduleItemRepository.save(item);
    }

    @Transactional
    public ScheduleItem reschedule(Long id, LocalDateTime newStartDateTime, 
                                   LocalDateTime newEndDateTime) {
        ScheduleItem item = getScheduleItemById(id);
        validateTimeRange(newStartDateTime, newEndDateTime);

        item.setStartDateTime(newStartDateTime);
        item.setEndDateTime(newEndDateTime);
        item.setUpdatedAt(LocalDateTime.now());

        return scheduleItemRepository.save(item);
    }

    @Transactional
    public ScheduleItem assignVenue(Long id, Long newVenueId) {
        ScheduleItem item = getScheduleItemById(id);
        
        Venue venue = venueRepository.findById(newVenueId)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Venue not found."));

        item.setVenue(venue);
        item.setUpdatedAt(LocalDateTime.now());

        return scheduleItemRepository.save(item);
    }

    @Transactional
    public void removeFromSchedule(Long id) {
        ScheduleItem item = getScheduleItemById(id);
        scheduleItemRepository.delete(item);
    }

    @Transactional(readOnly = true)
    public Duration getDuration(Long id) {
        ScheduleItem item = getScheduleItemById(id);
        return Duration.between(item.getStartDateTime(), item.getEndDateTime());
    }

    private void validateTimeRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (startDateTime == null || endDateTime == null || !startDateTime.isBefore(endDateTime)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid time range.");
        }
    }
}