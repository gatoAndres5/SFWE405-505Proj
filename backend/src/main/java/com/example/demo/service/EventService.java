package com.example.demo.service;
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.*;
import com.example.demo.repository.EventRepository;

@Service
@Transactional //check with team to see if they are also doing transactional
public class EventService {
    private final EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository){
        this.eventRepository = eventRepository;
    }    
    
    //helper method
    public Event getEventById(long eventId){
        return eventRepository.findById(eventId).orElseThrow(()-> new IllegalArgumentException("Event not found, id: "+ eventId));

    }

    //list helpers
    public List<Venue> listEventVenues(Long eventId){
        return getEventById(eventId).getVenues();
    }

    public List<Vendor> listEventVendors(Long eventId){
        return getEventById(eventId).getVendors();
    }

    public List<ScheduleItem> listEventScheduledItems(Long eventId){
        return getEventById(eventId).getScheduledItems();
    }
    //createEvent()
    //Create a new event
    public Event createEvent(String name, String description, LocalDateTime startDateTime, LocalDateTime endDateTime){
        Event event = new Event(name, description, startDateTime, endDateTime);
        return eventRepository.save(event);
    }

    //Read (CRUD) all
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    //updateEventDetails()
    //Update event details
    public Event updateEventDetails(Long eventId, String name, String description,  LocalDateTime startDateTime, LocalDateTime endDateTime){
        Event event = getEventById(eventId);
        //check the user is actually updating event correctly

        if (name != null && !name.trim().isEmpty()) event.setName(name);
        if (description != null && !description.trim().isEmpty()) event.setDescription(description);
        
        if (startDateTime !=null){
            if(startDateTime.isBefore(LocalDateTime.now())){
                throw new IllegalArgumentException("Start time of the event cannot be in the past");
            }
            event.setStartDateTime(startDateTime);
        }
        if (endDateTime !=null){
            if(endDateTime.isBefore(event.getStartDateTime())){
                throw new IllegalArgumentException("The event cannot end before it has started");
            }
            event.setEndDateTime(endDateTime);
        }
        event.setUpdateTime(LocalDateTime.now());
        return eventRepository.save(event);
    }


    //cancelEvent()
    //Delete in CRUD
    public void cancelEvent(Long eventId){
        Event event = getEventById(eventId);
        eventRepository.delete(event);
    }

    //addVenue(venueId)
    public Event addVenueToEvent(Long eventId, Venue venue){
        Event event = getEventById(eventId);
        event.addVenue(venue);
        return eventRepository.save(event);
    }

    //removeVenue(venueId)
    public Event removeVenueFromEvent(Long eventId, Venue venue){
        Event event = getEventById(eventId);
        event.removeVenue(venue);
        return eventRepository.save(event);
    }
    //addScheduleItem(scheduleItenData)
    public Event addScheduleItem(Long eventId, ScheduleItem scheduledItem){
        Event event = getEventById(eventId);
        scheduledItem.setEvent(event);
        event.getScheduledItems().add(scheduledItem);

        return eventRepository.save(event);
    }  
    //added REMOVE SCHEDULED ITEM
    public Event removeScheduleItem(Long eventId, ScheduleItem scheduledItem){
        Event event = getEventById(eventId);

        if(!event.getScheduledItems().contains(scheduledItem)){
            throw new  IllegalArgumentException("Scheduled item was not found in this event");
        }
        
        event.getScheduledItems().remove(scheduledItem);

        scheduledItem.setEvent(null);
        
        return eventRepository.save(event);
    }  
    //listParticipants()
    public List<Participant> listParticipants(Long eventId){
        Event event = getEventById(eventId);

        List<Participant> participants = new ArrayList<>();

        for (Registration registration: event.getRegistrations()){
            participants.add(registration.getParticipant());
        }

        return participants;
    }
    //listVendors()
    public List<Vendor> listVendors(Long eventId){
        Event event = getEventById(eventId);
        return event.getVendors();

    }

}
