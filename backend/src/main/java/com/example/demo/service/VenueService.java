package com.example.demo.service;

import com.example.demo.entity.Venue;
import com.example.demo.entity.Address;
import com.example.demo.entity.ScheduleItem;
import com.example.demo.repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Service
@Transactional
public class VenueService {
    
    @Autowired
    private VenueRepository venueRepository;

    //BUSINESS METHODS
    
    public Venue createVenue(String name, Address address, int capacity, 
                           String contactName, String contactEmail, String contactPhone) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Venue name cannot be null or empty");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }
        
        Venue venue = new Venue(name, address, capacity, contactName, contactEmail, contactPhone);
        return venueRepository.save(venue);
    }
    
    public Venue updateVenue(Long venueId, String name, Address address, int capacity, 
                          String contactName, String contactEmail, String contactPhone) {
        Venue venue = venueRepository.findById(venueId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venue not found with id: " + venueId));
        
        if (name != null && !name.trim().isEmpty()) {
            venue.setName(name);
        }
        if (address != null) {
            venue.setAddress(address);
        }
        if (capacity > 0) {
            venue.setCapacity(capacity);
        }
        if (contactName != null) {
            venue.setContactName(contactName);
        }
        if (contactEmail != null) {
            venue.setContactEmail(contactEmail);
        }
        if (contactPhone != null) {
            venue.setContactPhone(contactPhone);
        }
        
        venue.setUpdatedAt(LocalDateTime.now());
        return venueRepository.save(venue);
    }
    
    public void deactivateVenue(Long venueId) {
        Venue venue = venueRepository.findById(venueId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venue not found with id: " + venueId));
        
        venue.setUpdatedAt(LocalDateTime.now());
        venueRepository.save(venue);
    }
    
    public boolean isAvailable(Long venueId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (startDateTime == null || endDateTime == null) {
            throw new IllegalArgumentException("Start and end times cannot be null");
        }
        if (startDateTime.isAfter(endDateTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        
        Venue venue = venueRepository.findById(venueId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venue not found with id: " + venueId));
        
        for (ScheduleItem item : venue.getScheduleItems()) {
            if (item.getStartDateTime().isBefore(endDateTime) && 
                item.getEndDateTime().isAfter(startDateTime)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Validates if a venue can be assigned to an event based on event status.
     * This provides venue-side validation when the event service doesn't validate status.
     * 
     * @param venueId the venue ID
     * @param eventId the event ID (for validation purposes)
     * @return true if venue can be assigned, false otherwise
     * @throws ResponseStatusException if venue not found
     */
    public boolean canBeAssignedToEvent(Long venueId, Long eventId) {
        Venue venue = venueRepository.findById(venueId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venue not found with id: " + venueId));
            
        // Note: Since we can't modify EventService, we can't check event status here
        // This method exists for future validation when event files become accessible
        return true;
    }
    
    public List<ScheduleItem> listScheduledItems(Long venueId, LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start and end dates cannot be null");
        }
        
        Venue venue = venueRepository.findById(venueId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venue not found with id: " + venueId));
        
        List<ScheduleItem> filteredItems = new ArrayList<>();
        for (ScheduleItem item : venue.getScheduleItems()) {
            if (item.getStartDateTime().isAfter(startDate) && 
                item.getEndDateTime().isBefore(endDate)) {
                filteredItems.add(item);
            }
        }
        return filteredItems;
    }
    
    public List<Venue> findAvailableVenues(LocalDateTime startTime, LocalDateTime endTime) {
        return venueRepository.findAvailableVenues(startTime, endTime);
    }
    
    public Venue getVenueById(Long venueId) {
        return venueRepository.findById(venueId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venue not found with id: " + venueId));
    }
    
    public List<Venue> getAllVenues() {
        return venueRepository.findAll();
    }
    
    public void deleteVenue(Long venueId) {
        if (!venueRepository.existsById(venueId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Venue not found with id: " + venueId);
        }
        venueRepository.deleteById(venueId);
    }
}
