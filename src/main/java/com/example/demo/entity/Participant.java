package com.example.demo.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.time.Instant;

@Entity
@Table(name = "participants")
public class Participant {

    public enum Role {
        ATTENDEE,
        SPEAKER,
        STAFF
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id")
    private Long participantId;
    
    @Column(nullable = false, length = 80)
    private String firstName;

    @Column(nullable = false, length = 80)
    private String lastName;

    @Column(nullable = false, unique = true, length = 160)
    private String email;

    @Column(length = 32)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Role role;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "participant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Registration> registrations = new ArrayList<>();

    protected Participant() {}

    //timestamps
    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public static Participant createParticipant(String firstName,
        String lastName,
        String email,
        String phone,
        Role role) {
        Participant p = new Participant();
        p.firstName = requireNotBlank(firstName, "firstName");
        p.lastName = requireNotBlank(lastName, "lastName");
        p.email = requireNotBlank(email, "email");
        p.phone = (phone == null ? null : phone.trim());
        p.role = (role == null ? Role.ATTENDEE : role);
        p.active = true;
        return p;
    }

    public void updateContactInfo(String email, String phone) {
        if (email != null && !email.isBlank()) {
            this.email = email.trim();
        }
        if (phone != null) {
            this.phone = phone.trim();
        }
    }

    public void deactivateParticipant() {
        this.active = false;
    }

    public List<Registration> getRegistrations() {
        return Collections.unmodifiableList(registrations);
    }

    public Registration registerForEvent(Long eventId) {
        throw new UnsupportedOperationException(
            "registerForEvent(eventId) should be handled in a Service. Load the Event by id, then call registerForEvent(Event)."
        );
    }

     public Registration registerForEvent(Event event) {
        return registerForEvent(event, RegistrationStatus.CONFIRMED, false, null);
    }

    public Registration registerForEvent(Event event, RegistrationStatus status, boolean checkInStatus, String notes) {
        Objects.requireNonNull(event, "event");

        RegistrationStatus finalStatus = (status == null ? RegistrationStatus.CONFIRMED : status);

        Registration reg = new Registration(
            event,
            this,
            new Date(),
            finalStatus,
            checkInStatus,
            notes
        );

        this.registrations.add(reg);
        return reg;
    }

    //Getters/Setters:
    public Long getParticipantId() {
        return participantId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = requireNotBlank(firstName, "firstName");
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = requireNotBlank(lastName, "lastName");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = requireNotBlank(email, "email");
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = (phone == null ? null : phone.trim());
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = (role == null ? Role.ATTENDEE : role);
    }

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    protected List<Registration> getRegistrationsInternal() {
        return registrations;
    }

    private static String requireNotBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return value.trim();
    }
}
