package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.ScheduleItem;
import com.example.demo.service.ScheduleItemService;

/**
 * REST controller for managing schedule items within the event planning platform.
 * Provides endpoints for creating, updating, rescheduling, and retrieving schedule details.
 */

@RestController
@RequestMapping("/scheduleItems")
public class ScheduleItemController {

    private final ScheduleItemService scheduleItemService;

    public ScheduleItemController(ScheduleItemService scheduleItemService) {
        this.scheduleItemService = scheduleItemService;
    }

    /**
     * Creates a new schedule item for a specific event and venue.
     * Authorized Roles are ADMIN and ORGANIZER.
     *
     * @param eventId ID of the associated event
     * @param venueId ID of the assigned venue
     * @param title Title of the schedule item
     * @param description Brief description of the activity
     * @param startDateTime The scheduled start time
     * @param endDateTime The scheduled end time
     * @param type The category of the item (e.g., Workshop, Keynote)
     * @return The created ScheduleItem entity
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @PostMapping 
    public ScheduleItem createScheduleItem(@RequestParam Long eventId, 
                                           @RequestParam Long venueId, 
                                           @RequestParam String title, 
                                           @RequestParam String description,
                                           @RequestParam LocalDateTime startDateTime, 
                                           @RequestParam LocalDateTime endDateTime,
                                           @RequestParam String type) {
        return scheduleItemService.createScheduleItem(eventId, venueId, title, description, startDateTime, endDateTime, type);
    }

    /**
     * Retrieves a list of all schedule items in the system.
     * Accessible by ADMIN, ORGANIZER, STAFF, and PARTICIPANT.
     *
     * @return List of all ScheduleItems
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER', 'STAFF', 'PARTICIPANT')")
    @GetMapping
    public List<ScheduleItem> getAllScheduleItems() {
        return scheduleItemService.getAllScheduleItems();
    }

    /**
     * Retrieves the details of a specific schedule item by its ID.
     * Accessible by ADMIN, ORGANIZER, STAFF, and PARTICIPANT.
     *
     * @param id The ID of the schedule item to retrieve
     * @return The found ScheduleItem entity
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER', 'STAFF', 'PARTICIPANT')")
    @GetMapping("/{id}")
    public ScheduleItem getScheduleItemById(@PathVariable Long id) {
        return scheduleItemService.getScheduleItemById(id);
    }

    /**
     * Calculates and returns the duration of a specific schedule item.
     * Accessible by ADMIN, ORGANIZER, STAFF, and PARTICIPANT.
     *
     * @param id The ID of the schedule item
     * @return A Duration object representing the time between start and end
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER', 'STAFF', 'PARTICIPANT')")
    @GetMapping("/{id}/duration")
    public java.time.Duration getDuration(@PathVariable Long id) {
        return scheduleItemService.getDuration(id);
    }

    /**
     * Updates all details of an existing schedule item.
     * Authorized Roles are ADMIN and ORGANIZER.
     *
     * @param id The ID of the schedule item to update
     * @param eventId Updated event ID
     * @param venueId Updated venue ID
     * @param title Updated title
     * @param description Updated description
     * @param startDateTime Updated start time
     * @param endDateTime Updated end time
     * @param type Updated category/type
     * @return The updated ScheduleItem entity
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @PutMapping("/{id}")
    public ScheduleItem updateScheduleItem(@PathVariable Long id,
                                           @RequestParam Long eventId,
                                           @RequestParam Long venueId,
                                           @RequestParam String title,
                                           @RequestParam String description,
                                           @RequestParam LocalDateTime startDateTime,
                                           @RequestParam LocalDateTime endDateTime,
                                           @RequestParam String type) {
        return scheduleItemService.updateScheduleItem(id, eventId, venueId, title, description, startDateTime, endDateTime, type);
    }

    /**
     * Updates only the time window for a specific schedule item (Rescheduling).
     * Authorized Roles are ADMIN and ORGANIZER.
     *
     * @param id The ID of the schedule item
     * @param startDateTime The new start time
     * @param endDateTime The new end time
     * @return The rescheduled ScheduleItem entity
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @PutMapping("/{id}/reschedule")
    public ScheduleItem rescheduleItem(@PathVariable Long id,
                                       @RequestParam LocalDateTime startDateTime,
                                       @RequestParam LocalDateTime endDateTime) {
        return scheduleItemService.reschedule(id, startDateTime, endDateTime);
    }

    /**
     * Changes the venue assigned to a specific schedule item.
     * Authorized Roles are ADMIN and ORGANIZER.
     *
     * @param id The ID of the schedule item
     * @param venueId The new venue ID to be assigned
     * @return The updated ScheduleItem entity
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @PutMapping("/{id}/assignVenue")
    public ScheduleItem assignVenue(@PathVariable Long id,
                                    @RequestParam Long venueId) {
        return scheduleItemService.assignVenue(id, venueId);
    }

    /**
     * Permanently removes a schedule item from the system.
     * Authorized Roles are ADMIN and ORGANIZER.
     *
     * @param id The ID of the schedule item to delete
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @DeleteMapping("/{id}") 
    public void removeFromSchedule(@PathVariable Long id) {
        scheduleItemService.removeFromSchedule(id);
    }
}
