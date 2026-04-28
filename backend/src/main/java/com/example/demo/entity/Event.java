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

@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private LocalDateTime startDateTime;

    @NotNull
    private LocalDateTime endDateTime;

    @Enumerated(EnumType.STRING)
    private EventStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ScheduleItem> scheduledItems = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Registration> registrations = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "event_vendor",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "vendor_id")
    )
    private List<Vendor> vendors = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "event_venue",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "venue_id")
    )
    private List<Venue> venues = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventAssignment> eventAssignments = new ArrayList<>();

    protected Event() {}


    public Event(String name, String description, LocalDateTime startDateTime, LocalDateTime endDateTime){
        this.name = name;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.status = EventStatus.DRAFT;
    }

    public Event(String name, String description, EventStatus status,
                 LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.status == null) {
            this.status = EventStatus.DRAFT;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
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

    public void addVenue(Venue venue) {
        if (!this.venues.contains(venue)) {
            this.venues.add(venue);
        }
        if (!venue.getEvents().contains(this)) {
            venue.getEvents().add(this);
        }
    }

    public void removeVenue(Venue venue) {
        this.venues.remove(venue);
        venue.getEvents().remove(this);
    }

    public void addScheduleItem(ScheduleItem item) {
        if (!this.scheduledItems.contains(item)) {
            this.scheduledItems.add(item);
        }
        item.setEvent(this);
    }

    public void removeScheduleItem(ScheduleItem item) {
        this.scheduledItems.remove(item);
        item.setEvent(null);
    }
}