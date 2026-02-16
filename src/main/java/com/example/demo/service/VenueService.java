package com.example.demo.service;

import com.example.demo.entity.Venue;
import com.example.demo.entity.ScheduleItem;
import com.example.demo.repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Service
@Transactional
public class VenueService {
    
    @Autowired
    private VenueRepository venueRepository;

    //BUSINESS METHODS
    
    public Venue createVenue(String name, String address, int capacity, 
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
    
    public Venue updateVenue(Long venueId, String name, String address, int capacity, 
                          String contactName, String contactEmail, String contactPhone) {
        Venue venue = venueRepository.findById(venueId)
            .orElseThrow(() -> new IllegalArgumentException("Venue not found with id: " + venueId));
        
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
            .orElseThrow(() -> new IllegalArgumentException("Venue not found with id: " + venueId));
        
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
            .orElseThrow(() -> new IllegalArgumentException("Venue not found with id: " + venueId));
        
        for (ScheduleItem item : venue.getScheduleItems()) {
            if (item.getStartTime().isBefore(endDateTime) && 
                item.getEndTime().isAfter(startDateTime)) {
                return false;
            }
        }
        return true;
    }
    
    public List<ScheduleItem> listScheduledItems(Long venueId, LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start and end dates cannot be null");
        }
        
        Venue venue = venueRepository.findById(venueId)
            .orElseThrow(() -> new IllegalArgumentException("Venue not found with id: " + venueId));
        
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
            .orElseThrow(() -> new IllegalArgumentException("Venue not found with id: " + venueId));
    }
    
    public List<Venue> getAllVenues() {
        return venueRepository.findAll();
    }
    
    public void deleteVenue(Long venueId) {
        if (!venueRepository.existsById(venueId)) {
            throw new IllegalArgumentException("Venue not found with id: " + venueId);
        }
        venueRepository.deleteById(venueId);
    }
}
