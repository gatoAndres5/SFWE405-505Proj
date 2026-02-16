package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import java.time.LocalDateTime;


@Entity
public class ScheduleItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Integer eventId;
    
    @ManyToOne
    @JoinColumn(name = "venue_id")
    private Venue venueId;

    private String title;
    private String description;

    private String startDateTime;
    private String endDateTime;

    private String type;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor
    protected ScheduleItem() {}

    public ScheduleItem(Integer eventId, Venue venueId, String title, String description, String startDateTime, String endDateTime, String type, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.eventId = eventId;
        this.venueId = venueId;
        this.title = title;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.type = type;
        this.createdAt = createdAt;
        //maybe remove later, or leave it blank when ScheduleItem object is created
        this.updatedAt = updatedAt;
    }

    //Getters
    public Long getId() {
        return id;
    }
    public Integer getEventId() {
        return eventId;
    }
    public Venue getVenue() {
        return venueId;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public String getStartDateTime() {
        return startDateTime;
    }
    public String getEndDateTime() {
        return endDateTime;
    }
    public String getType() {
        return type;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    //Setters
    public void setId(Long id) {
        this.id = id;
    }
    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }
    public void setVenueId(Venue venueId) {
        this.venueId = venueId;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }
    public void setEndDateTime(String endDateTime) {
        this.endDateTime = endDateTime;
    }
    public void setType(String type) {
        this.type = type;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}