package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class EventAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    public enum EventAssignmentRole {
        ORGANIZER,
        STAFF
    }   

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventAssignmentRole role;

    @Column(nullable = false)
    private boolean active = true;

    protected EventAssignment() {
    }

    public EventAssignment(Event event, User user, EventAssignmentRole role) {
        this.event = event;
        this.user = user;
        this.role = role;
        this.active = true;
    }

    public Long getId() {
        return id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public EventAssignmentRole getRole() {
        return role;
    }

    public void setRole(EventAssignmentRole role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}