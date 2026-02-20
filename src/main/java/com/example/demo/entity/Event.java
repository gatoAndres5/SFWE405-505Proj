package com.example.demo.entity;

import com.example.demo.entity.EventStatus;
import java.util.List;

import jakarta.persistence.CascadeType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    @Enumerated(EnumType.STRING)
    private EventStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /*Event → Schedule Item: One-to-many (one event has many schedule items)
    Event ↔ Participant: Many-to-many (through Registration/RSVP)
    Event ↔ Vendor: Many-to-many (through Contract/Booking) */

    //also need venue relationship for method

    //relationships

    @OneToMany(mappedBy = "eventId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScheduleItem> scheduledItems = new ArrayList<>();

/*  won't show the participant and will delete all of the RSVP information
    @ManyToMany   
    @JoinTable(name= "event_registration", joinColumns = @JoinColumn(name="event_id"), inverseJoinColumns = @JoinColumn(name= "registration_id"))
    private List<Registration> registrations = new ArrayList<>();
*/
/* No link to registration just to know who is going where 
   @ManyToMany   
    @JoinTable(name= "event_registration", joinColumns = @JoinColumn(name="event_id"), inverseJoinColumns = @JoinColumn(name= "participant_id"))
    private List<Participant> participants = new ArrayList<>();
*/

//one event and many registrations
//then one participant and many registrations 
//this would make it "many to many" through registrations

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Registration> registrations = new ArrayList<>();
  
    @ManyToMany
    @JoinTable(name= "event_vendor", joinColumns = @JoinColumn(name="event_id"), inverseJoinColumns = @JoinColumn(name= "vendor_id"))
    private List<Vendor> vendors = new ArrayList<>();

    @ManyToMany
    @JoinTable(name= "event_venue", joinColumns = @JoinColumn(name="event_id"), inverseJoinColumns = @JoinColumn(name= "venue_id"))
    private List<Venue> venues = new ArrayList<>();

    //constructors

    protected Event(){}

    public Event(String name, String description, LocalDateTime startDateTime, LocalDateTime endDateTime){
        this.name = name;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime =endDateTime;
        this.status = EventStatus.DRAFT; //default
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    public Event(String name, String description, EventStatus status, LocalDateTime startDateTime, LocalDateTime endDateTime){
        this.name = name;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime =endDateTime;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    //getters

    public String getName(){
        return this.name;
    }

    public String getDescription(){
        return this.description;
    }

    public LocalDateTime getStartDateTime(){
        return this.startDateTime;
    }
    
    public LocalDateTime getEndDateTime(){
        return this.endDateTime;
    }

    public EventStatus getStatus(){
        return this.status;
    }

    public LocalDateTime getCreationTime(){
        return this.createdAt;
    }

    public LocalDateTime getUpdateTime(){
        return this.updatedAt;
    }

    //setters
     public void setName(String name){
        this.name = name;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setStartDateTime(LocalDateTime startDateTime){
        this.startDateTime = startDateTime;
    }
    
    public void setEndDateTime(LocalDateTime endDateTime){
        this.endDateTime = endDateTime;
    }

    public void setStatus(EventStatus status){
        this.status = status;
    }

    public void setCreationTime(LocalDateTime createdAt){
        this.createdAt = createdAt;
    }

    public void setUpdateTime(LocalDateTime updatedAt){
        this.updatedAt = updatedAt;
    }

}
