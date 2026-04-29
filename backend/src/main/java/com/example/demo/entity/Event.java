package com.example.demo.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
/**
 * Represents an event in the system.
 * Stores event attributes like id, name
 * description, start date and time,  
 * end date and time, associated venues,
 * vendors, scheduled items and registrations.
 */
@Entity
public class Event {

    /** Unique identifier for event */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**Name of event */
    @NotBlank
    private String name;

    /**Description of event */
    @NotBlank
    private String description;

    /**Start date and time of event */
    @NotNull
    private LocalDateTime startDateTime;

    /**End date and time of event */
    @NotNull
    private LocalDateTime endDateTime;

    /**Status of event: DRAFT, ACTIVE OR CANCELLED*/
    @Enumerated(EnumType.STRING)
    private EventStatus status;

    /**Timestamp when the event was created */
    private LocalDateTime createdAt;

    /**Timestamp when the event was updated */
    private LocalDateTime updatedAt;

    /**List of schedule items associated with the event (ignored in json)*/
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ScheduleItem> scheduledItems = new ArrayList<>();

    /**List of registrations in event (ignored in json) */
    @JsonIgnore
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Registration> registrations = new ArrayList<>();

    /**List of vendors associated with event */
    @ManyToMany
    @JoinTable(
        name = "event_vendor",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "vendor_id")
    )
    private List<Vendor> vendors = new ArrayList<>();

    /**List of venues associated with event */
    @ManyToMany
    @JoinTable(
        name = "event_venue",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "venue_id")
    )
    private List<Venue> venues = new ArrayList<>();

    /**List of assignments in event */
    @JsonIgnore
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventAssignment> assignments = new ArrayList<>();


    /**Default constructor required by JPA */
    protected Event() {}

    /**
     * Creates event with required details.
     * @param name name of the event
     * @param description description of the event
     * @param startDateTime start time of event
     * @param endDateTime end time of event
     */
    public Event(String name, String description, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.name = name;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.status = EventStatus.DRAFT;
    }
    /**
     * Creates event with explicit status.
     * @param name
     * @param description
     * @param status
     * @param startDateTime
     * @param endDateTime
     */
    public Event(String name, String description, EventStatus status,
                 LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    /** Called automatically by JPA before the entity is persisted into the database.
     * Sets the creation and update timestamp to current time and assigns a default 
     * status (DRAFT) if the status has not been previously set.
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.status == null) {
            this.status = EventStatus.DRAFT;
        }
    }

    /** 
     * Called automatically by JPA before the event is updated in the database.
     * Updates the "updatedAt" timestamp to current time. 
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    public List<EventAssignment> getAssignments() {
    return assignments;
    }
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public EventStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreationTime() {
        return createdAt;
    }

    public LocalDateTime getUpdateTime() {
        return updatedAt;
    }

    public List<ScheduleItem> getScheduledItems() {
        return scheduledItems;
    }

    public List<Registration> getRegistrations() {
        return registrations;
    }

    public List<Vendor> getVendors() {
        return vendors;
    }

    @JsonIgnore
    public List<Venue> getVenues() {
        return venues;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public void setCreationTime(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdateTime(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setVenues(List<Venue> venues) {
        this.venues = venues;
    }

    public void setVendors(List<Vendor> vendors) {
        this.vendors = vendors;
    }

    /**
     * Add venue to event and ensures bidirectional consistency with venue class.
     * @param venue venue to add into event
     */
    public void addVenue(Venue venue) {
        if (!this.venues.contains(venue)) {
            this.venues.add(venue);
        }
        if (!venue.getEvents().contains(this)) {
            venue.getEvents().add(this);
        }
    }
    /**
     * Removes venue from event.
     * @param venue venue to remove
     */
    public void removeVenue(Venue venue) {
        this.venues.remove(venue);
        venue.getEvents().remove(this);
    }

    /**
     * Adds a schedule item to event and sets the event for the item.
     * @param item
     */
    public void addScheduleItem(ScheduleItem item) {
        if (!this.scheduledItems.contains(item)) {
            this.scheduledItems.add(item);
        }
        item.setEvent(this);
    }

    /**
     * Removes a scheduled item from event.
     * @param item item to remove from event 
     */
    public void removeScheduleItem(ScheduleItem item) {
        this.scheduledItems.remove(item);
        item.setEvent(null);
    }
}