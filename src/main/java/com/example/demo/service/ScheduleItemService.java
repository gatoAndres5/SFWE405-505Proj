package com.example.demo.service;

import com.example.demo.entity.Event;
import com.example.demo.entity.ScheduleItem;
import com.example.demo.entity.Venue;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.ScheduleItemRepository;
import com.example.demo.repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ScheduleItemService {

    @Autowired
    private ScheduleItemRepository scheduleItemRepository;
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private VenueRepository venueRepository;
    
    @Autowired
    private VenueService venueService;

    //check if schedule items exist
    public List<ScheduleItem> getAllScheduleItems() {
        return scheduleItemRepository.findAll();
    }

    public ScheduleItem getScheduleItemById(Long id) {
        return scheduleItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Schedule Item not found with id: " + id));
    }
    
    //bussiness methods
    //creates a new schedule item
    public ScheduleItem createScheduleItem(Long eventId, Long venueId, String title, String description,
                                           LocalDateTime startDateTime, LocalDateTime endDateTime, String type) {

        Event event = findEventById(eventId);
        Venue venue = findVenueById(venueId);
        validateTimeRange(startDateTime, endDateTime);
        validateVenueAvailability(venueId, startDateTime, endDateTime);

        ScheduleItem scheduleItem = new ScheduleItem(event, venue, title, description, startDateTime, endDateTime, type);
        return scheduleItemRepository.save(scheduleItem);
    }

    //updates an existing schedule item
    public ScheduleItem updateScheduleItem(Long scheduleItemId, Long eventId, Long venueId, String title,
                                           String description, LocalDateTime startDateTime, LocalDateTime endDateTime, String type) {

        ScheduleItem existingItem = getScheduleItemById(scheduleItemId);
        Event event = findEventById(eventId);
        Venue venue = findVenueById(venueId);
        validateTimeRange(startDateTime, endDateTime);
        validateVenueAvailability(venueId, startDateTime, endDateTime);

        updateScheduleItemFields(existingItem, event, venue, title, description, startDateTime, endDateTime, type);
        return scheduleItemRepository.save(existingItem);
    }

    //deletes a schedule item
    public void deleteScheduleItem(Long scheduleItemId) {
        ScheduleItem existingItem = getScheduleItemById(scheduleItemId);
        scheduleItemRepository.delete(existingItem);
    }
    
    //reschedules an existing item to new time slots
    public ScheduleItem reschedule(Long scheduleItemId, LocalDateTime newStart, LocalDateTime newEnd) {
        ScheduleItem existingItem = getScheduleItemById(scheduleItemId);
        validateTimeRange(newStart, newEnd);
        validateVenueAvailability(existingItem.getVenue().getVenueId(), newStart, newEnd);
        
        existingItem.setStartDateTime(newStart);
        existingItem.setEndDateTime(newEnd);
        existingItem.setUpdatedAt(LocalDateTime.now());
        return scheduleItemRepository.save(existingItem);
    }
    
    //assigns a new venue to the schedule item
    public ScheduleItem assignVenue(Long scheduleItemId, Long newVenueId) {
        ScheduleItem existingItem = getScheduleItemById(scheduleItemId);
        Venue newVenue = findVenueById(newVenueId);
        
        validateVenueAvailability(newVenueId, existingItem.getStartDateTime(), existingItem.getEndDateTime());
        
        existingItem.setVenue(newVenue);
        existingItem.setUpdatedAt(LocalDateTime.now());
        
        return scheduleItemRepository.save(existingItem);
    }
    
    //Removes a schedule item from the schedule
    public void removeFromSchedule(Long scheduleItemId) {
        deleteScheduleItem(scheduleItemId);
    }
    
    //return duration between start and end time
    public Duration getDuration(Long scheduleItemId) {
        ScheduleItem item = getScheduleItemById(scheduleItemId);
        return Duration.between(item.getStartDateTime(), item.getEndDateTime());
    }

    //helper methods
    private Event findEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with id: " + eventId));
    }
    
    private Venue findVenueById(Long venueId) {
        return venueRepository.findById(venueId)
                .orElseThrow(() -> new IllegalArgumentException("Venue not found with id: " + venueId));
    }
    
    private void validateVenueAvailability(Long venueId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (!venueService.isAvailable(venueId, startDateTime, endDateTime)) {
            throw new IllegalArgumentException("Venue is not available during the requested time slot.");
        }
    }
    
    private void updateScheduleItemFields(ScheduleItem item, Event event, Venue venue, String title,
                                         String description, LocalDateTime startDateTime, LocalDateTime endDateTime, String type) {
        item.setEvent(event);
        item.setVenue(venue);
        item.setTitle(title);
        item.setDescription(description);
        item.setStartDateTime(startDateTime);
        item.setEndDateTime(endDateTime);
        item.setType(type);
        item.setUpdatedAt(LocalDateTime.now());
    }

    private void validateTimeRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (startDateTime == null || endDateTime == null) {
            throw new IllegalArgumentException("Start time and end time are required.");
        }

        if (!startDateTime.isBefore(endDateTime)) {
            throw new IllegalArgumentException("Start time must be before end time.");
        }
    }
}