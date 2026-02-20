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
    private Event event;
    
    @ManyToOne
    @JoinColumn(name = "venue_id")
    private Venue venue;

    private String title;
    private String description;

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    private String type;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor
    protected ScheduleItem() {}

    public ScheduleItem(Event event, Venue venue,
                        String title, String description,
                        LocalDateTime startDateTime, LocalDateTime endDateTime, 
                        String type) {
        this.event = event;
        this.venue = venue;
        this.title = title;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.type = type;
        this.createdAt = LocalDateTime.now();
        //maybe remove later, or leave it blank when ScheduleItem object is created
        this.updatedAt = LocalDateTime.now();
    }

    //Getters
    public Long getId() {
        return id;
    }
    public Event getEvent() {
        return event;
    }
    public Venue getVenue() {
        return venue;
    }
    public String getTitle() {
        return title;
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
    public void setEvent(Event event) {
        this.event = event;
    }
    public void setVenue(Venue venue) {
        this.venue = venue;
    }
    public void setTitle(String title) {
        this.title = title;
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