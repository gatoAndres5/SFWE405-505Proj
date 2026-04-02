package com.example.demo.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.ScheduleItem;
import com.example.demo.service.ScheduleItemService;

@RestController
@RequestMapping("/schedule-items") //get all schedule items
public class ScheduleItemController {

    @Autowired
    private ScheduleItemService scheduleItemService;

    @GetMapping
    public List<ScheduleItem> getAllScheduleItems() {
        return scheduleItemService.getAllScheduleItems();
    }

    @GetMapping("/{id}") //get schedule item by id
    public ScheduleItem getScheduleItemById(@PathVariable Long id) {
        return scheduleItemService.getScheduleItemById(id);
    }

    @PostMapping //create schedule item
    public ScheduleItem createScheduleItem(@RequestBody ScheduleItemRequest request) {
        return scheduleItemService.createScheduleItem(
            request.eventId, request.venueId, request.title, request.description,
            request.startDateTime, request.endDateTime, request.type
        );
    }

    @PutMapping("/{id}") //update schedule item
    public ScheduleItem updateScheduleItem(@PathVariable Long id, @RequestBody ScheduleItemRequest request) {
        return scheduleItemService.updateScheduleItem(
            id, request.eventId, request.venueId, request.title, request.description,
            request.startDateTime, request.endDateTime, request.type
        );
    }

    @DeleteMapping("/{id}") //delete schedule item
    public void deleteScheduleItem(@PathVariable Long id) {
        scheduleItemService.deleteScheduleItem(id);
    }

    @PutMapping("/{id}/reschedule") //reschedule an existing item
    public ScheduleItem rescheduleItem(@PathVariable Long id, @RequestBody RescheduleRequest request) {
        return scheduleItemService.reschedule(id, request.newStart, request.newEnd);
    }

    @PutMapping("/{id}/assign-venue") //assign a new venue to schedule item
    public ScheduleItem assignVenue(@PathVariable Long id, @RequestBody AssignVenueRequest request) {
        return scheduleItemService.assignVenue(id, request.venueId);
    }

    @GetMapping("/{id}/duration") //get duration of schedule item
    public java.time.Duration getDuration(@PathVariable Long id) {
        return scheduleItemService.getDuration(id);
    }

    //request body for schedule item operations
    public static class ScheduleItemRequest {
        public Long eventId;
        public Long venueId;
        public String title;
        public String description;
        public java.time.LocalDateTime startDateTime;
        public java.time.LocalDateTime endDateTime;
        public String type;
    }

    public static class RescheduleRequest {
        public java.time.LocalDateTime newStart;
        public java.time.LocalDateTime newEnd;
    }

    public static class AssignVenueRequest {
        public Long venueId;
    }
}
