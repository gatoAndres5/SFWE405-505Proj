package com.example.demo.controller;

import com.example.demo.entity.Venue;
import com.example.demo.entity.ScheduleItem;
import com.example.demo.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/venues")
public class VenueController {

    private final VenueService venueService;

    @Autowired
    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    @PostMapping
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

    @PutMapping("/{id}")
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVenue(@PathVariable Long id) {
        venueService.deleteVenue(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateVenue(@PathVariable Long id) {
        venueService.deactivateVenue(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venue> getVenue(@PathVariable Long id) {
        Venue venue = venueService.getVenueById(id);
        return ResponseEntity.ok(venue);
    }

    @GetMapping
    public ResponseEntity<List<Venue>> getAllVenues() {
        List<Venue> venues = venueService.getAllVenues();
        return ResponseEntity.ok(venues);
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<AvailabilityResponse> checkAvailability(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime) {
        
        boolean isAvailable = venueService.isAvailable(id, startDateTime, endDateTime);
        return ResponseEntity.ok(new AvailabilityResponse(isAvailable));
    }

    @GetMapping("/{id}/schedule")
    public ResponseEntity<List<ScheduleItem>> getSchedule(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        List<ScheduleItem> scheduleItems = venueService.listScheduledItems(id, startDate, endDate);
        return ResponseEntity.ok(scheduleItems);
    }

    @GetMapping("/available")
    public ResponseEntity<List<Venue>> findAvailableVenues(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        List<Venue> availableVenues = venueService.findAvailableVenues(startTime, endTime);
        return ResponseEntity.ok(availableVenues);
    }

    // DTO Classes
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

    public static class AvailabilityResponse {
        private boolean available;

        public AvailabilityResponse(boolean available) {
            this.available = available;
        }

        public boolean isAvailable() { return available; }
        public void setAvailable(boolean available) { this.available = available; }
    }
}
