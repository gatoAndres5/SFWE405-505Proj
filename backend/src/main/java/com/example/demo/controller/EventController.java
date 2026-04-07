package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Event;
import com.example.demo.entity.Participant;
import com.example.demo.entity.ScheduleItem;
import com.example.demo.entity.Vendor;
import com.example.demo.entity.Venue;
import com.example.demo.service.EventService;

@RestController
@RequestMapping("/events")
public class EventController {


    
    private final EventService eventService;

    public EventController(EventService eventService){
        this.eventService = eventService;
    }

    public static class EventRequest{
        public String name;
        public String description;
        public LocalDateTime startDateTime;
        public LocalDateTime endDateTime;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Event createEvent(@RequestBody EventRequest request) {
        return eventService.createEvent(request.name, request.description, request.startDateTime, request.endDateTime);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/{eventId}")
    public Event getEventById(@PathVariable Long eventId) {
        return eventService.getEventById(eventId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{eventId}")
    public Event updateEvent(@PathVariable Long eventId, @RequestBody EventRequest request){
        return eventService.updateEventDetails(eventId, request.name, request.description, request.startDateTime, request.endDateTime);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{eventId}")
    public void cancelEvent(@PathVariable Long eventId){
        eventService.cancelEvent(eventId);

    }

    //venue
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{eventId}/venues")
    public Event addVenue(@PathVariable Long eventId, @RequestBody Venue venue){
        return eventService.addVenueToEvent(eventId, venue);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{eventId}/venues")
    public Event cancelVenue(@PathVariable Long eventId, @RequestBody Venue venue){
        return eventService.removeVenueFromEvent(eventId, venue);

    }

    //schedule items
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{eventId}/schedule")
    public Event addScheduleItem(@PathVariable Long eventId, @RequestBody ScheduleItem item){
        return eventService.addScheduleItem(eventId, item);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{eventId}/schedule")
    public Event cancelScheduleItem(@PathVariable Long eventId, @RequestBody ScheduleItem item){
        return eventService.removeScheduleItem(eventId, item);
    }

    //lists 

    @GetMapping("/{eventId}/participants")
    public List <Participant> listParticipants(@PathVariable Long eventId){
        return eventService.listParticipants(eventId);
    }

    @GetMapping("/{eventId}/vendors")
    public List <Vendor> listVendors(@PathVariable Long eventId){
        return eventService.listEventVendors(eventId);
    }

    @GetMapping("/{eventId}/venues")
    public List <Venue> listVenues(@PathVariable Long eventId){
        return eventService.listEventVenues(eventId);
    }

    @GetMapping("/{eventId}/scheduleitems")
    public List <ScheduleItem> listScheduleItems(@PathVariable Long eventId){
        return eventService.listEventScheduledItems(eventId);
    }


   /*  @PostMapping
    public Event createEvent(@RequestBody Event event) {
        return eventRepository.save(event);
    }

    @GetMapping
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @GetMapping("/{id}")
    public Event getEvent(@PathVariable Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
    }*/
}
