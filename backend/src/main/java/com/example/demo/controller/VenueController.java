package com.example.demo.controller;

import com.example.demo.entity.Venue;
import com.example.demo.entity.ScheduleItem;
import com.example.demo.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller for managing venues within the event planning platform.
 * Provides endpoints for creating, updating, deactivating, and retrieving venue information
 * as well as checking availability and managing venue schedules.
 * 
 * @version 1.0
 * @since 2026-04-08
 */
@RestController
@RequestMapping("/venues")
public class VenueController {

    private final VenueService venueService;

    /**
     * Constructs a VenueController with the required VenueService dependency.
     * 
     * @param venueService the service layer for venue business operations
     */
    @Autowired
    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    /**
     * Creates a new venue in the system.
     * Validates the input parameters and persists the venue to the database.
     * 
     * @param request the venue creation request containing all required venue details
     * @return ResponseEntity containing the created venue with generated ID
     * @throws IllegalArgumentException if name is null/empty or capacity is invalid
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Venue> createVenue(@RequestBody CreateVenueRequest request) {
        Venue venue = venueService.createVenue(
            request.getName(),
            request.getAddress(),
            request.getCapacity(),
            request.getContactName(),
            request.getContactEmail(),
            request.getContactPhone()
        );
        return ResponseEntity.ok(venue);
    }

    /**
     * Updates an existing venue with new information.
     * Only non-null fields from the request will be updated.
     * 
     * @param id the unique identifier of the venue to update
     * @param request the venue update request containing fields to modify
     * @return ResponseEntity containing the updated venue information
     * @throws IllegalArgumentException if venue with given ID is not found
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Venue> updateVenue(@PathVariable Long id, @RequestBody UpdateVenueRequest request) {
        Venue venue = venueService.updateVenue(
            id,
            request.getName(),
            request.getAddress(),
            request.getCapacity(),
            request.getContactName(),
            request.getContactEmail(),
            request.getContactPhone()
        );
        return ResponseEntity.ok(venue);
    }

    /**
     * Permanently deletes a venue from the system.
     * This action cannot be undone and will remove all associated data.
     * 
     * @param id the unique identifier of the venue to delete
     * @return ResponseEntity with no content status (204)
     * @throws IllegalArgumentException if venue with given ID is not found
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteVenue(@PathVariable Long id) {
        venueService.deleteVenue(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deactivates a venue (soft delete).
     * The venue remains in the database but is marked as inactive.
     * 
     * @param id the unique identifier of the venue to deactivate
     * @return ResponseEntity with OK status (200)
     * @throws IllegalArgumentException if venue with given ID is not found
     */
    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateVenue(@PathVariable Long id) {
        venueService.deactivateVenue(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Retrieves a specific venue by its unique identifier.
     * 
     * @param id the unique identifier of the venue to retrieve
     * @return ResponseEntity containing the venue details
     * @throws IllegalArgumentException if venue with given ID is not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Venue> getVenue(@PathVariable Long id) {
        Venue venue = venueService.getVenueById(id);
        return ResponseEntity.ok(venue);
    }

    /**
     * Retrieves all venues in the system.
     * Returns a list of all venues regardless of their active status.
     * 
     * @return ResponseEntity containing a list of all venues
     */
    @GetMapping
    public ResponseEntity<List<Venue>> getAllVenues() {
        List<Venue> venues = venueService.getAllVenues();
        return ResponseEntity.ok(venues);
    }

    /**
     * Checks if a venue is available for a specific time range.
     * Validates that the venue exists and the time range is valid.
     * 
     * @param id the unique identifier of the venue to check
     * @param startDateTime the start of the time range to check
     * @param endDateTime the end of the time range to check
     * @return ResponseEntity containing availability status (true if available)
     * @throws IllegalArgumentException if venue not found or time range is invalid
     */
    @GetMapping("/{id}/availability")
    public ResponseEntity<AvailabilityResponse> checkAvailability(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime) {
        
        boolean isAvailable = venueService.isAvailable(id, startDateTime, endDateTime);
        return ResponseEntity.ok(new AvailabilityResponse(isAvailable));
    }

    /**
     * Retrieves all scheduled items for a venue within a specific date range.
     * Returns schedule items that fall completely within the specified range.
     * 
     * @param id the unique identifier of the venue
     * @param startDate the start date of the range to search
     * @param endDate the end date of the range to search
     * @return ResponseEntity containing a list of scheduled items
     * @throws IllegalArgumentException if venue not found or date range is invalid
     */
    @GetMapping("/{id}/schedule")
    public ResponseEntity<List<ScheduleItem>> getSchedule(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        List<ScheduleItem> scheduleItems = venueService.listScheduledItems(id, startDate, endDate);
        return ResponseEntity.ok(scheduleItems);
    }

    /**
     * Finds all venues that are available for a specific time range.
     * Searches across all venues and returns those without scheduling conflicts.
     * 
     * @param startTime the start time of the desired booking period
     * @param endTime the end time of the desired booking period
     * @return ResponseEntity containing a list of available venues
     */
    @GetMapping("/available")
    public ResponseEntity<List<Venue>> findAvailableVenues(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        List<Venue> availableVenues = venueService.findAvailableVenues(startTime, endTime);
        return ResponseEntity.ok(availableVenues);
    }

    // DTO Classes
    
    /**
     * Data Transfer Object for creating a new venue.
     * Contains all required fields for venue creation.
     */
    public static class CreateVenueRequest {
        private String name;
        private String address;
        private int capacity;
        private String contactName;
        private String contactEmail;
        private String contactPhone;

        // Getters
        public String getName() { return name; }
        public String getAddress() { return address; }
        public int getCapacity() { return capacity; }
        public String getContactName() { return contactName; }
        public String getContactEmail() { return contactEmail; }
        public String getContactPhone() { return contactPhone; }

        // Setters
        public void setName(String name) { this.name = name; }
        public void setAddress(String address) { this.address = address; }
        public void setCapacity(int capacity) { this.capacity = capacity; }
        public void setContactName(String contactName) { this.contactName = contactName; }
        public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
        public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    }

    /**
     * Data Transfer Object for updating an existing venue.
     * All fields are optional - only provided fields will be updated.
     */
    public static class UpdateVenueRequest {
        private String name;
        private String address;
        private int capacity;
        private String contactName;
        private String contactEmail;
        private String contactPhone;

        // Getters
        public String getName() { return name; }
        public String getAddress() { return address; }
        public int getCapacity() { return capacity; }
        public String getContactName() { return contactName; }
        public String getContactEmail() { return contactEmail; }
        public String getContactPhone() { return contactPhone; }

        // Setters
        public void setName(String name) { this.name = name; }
        public void setAddress(String address) { this.address = address; }
        public void setCapacity(int capacity) { this.capacity = capacity; }
        public void setContactName(String contactName) { this.contactName = contactName; }
        public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
        public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    }

    /**
     * Data Transfer Object for venue availability response.
     * Contains the availability status for the queried time range.
     */
    public static class AvailabilityResponse {
        private boolean available;

        /**
         * Constructs an availability response.
         * 
         * @param available true if the venue is available, false otherwise
         */
        public AvailabilityResponse(boolean available) {
            this.available = available;
        }

        /**
         * Gets the availability status.
         * 
         * @return true if available, false otherwise
         */
        public boolean isAvailable() { return available; }
        
        /**
         * Sets the availability status.
         * 
         * @param available the availability status to set
         */
        public void setAvailable(boolean available) { this.available = available; }
    }
}
