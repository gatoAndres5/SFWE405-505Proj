package com.example.demo.entity;
import java.util.Date;

import jakarta.persistence.*;

@Entity
public class Registration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(optional = false)
    @JoinColumn(name = "participant_id")
    private Participant participant;

    private Date registeredAt;

    @Enumerated(EnumType.STRING)
    private RegistrationStatus registrationStatus;

    private boolean checkInStatus;

    private String notes;



    protected Registration(){}

    public Registration(Event event, Participant participant, Date registeredAt, RegistrationStatus registrationStatus, boolean checkInStatus, String notes){
        this.event = event;
        this.participant = participant;
        this.registeredAt = registeredAt;
        this.registrationStatus = registrationStatus;
        this.checkInStatus = checkInStatus;
        this.notes = notes;
    }

    // GETTERS

    public Event getEvent(){
        return this.event;
    }

    public Participant getParticipant(){
        return this.participant;
    }

    public Date getRegisteredAt(){
        return this.registeredAt;
    }

    public RegistrationStatus getRegistrationStatus(){
        return this.registrationStatus;
    }

    public boolean getCheckInStatus(){
        return this.checkInStatus;
    }

    public String getNotes(){
        return this.notes;
    }

    //SETTERS

    public void setEvent(Event event){
        this.event = event;
    }

    public void setParticipant(Participant participant){
        this.participant =  participant;
    }

    public void setRegisteredAt(Date registeredAt){
        this.registeredAt =  registeredAt;
    }

    public void setRegistrationStatus(RegistrationStatus registrationStatus){
        this.registrationStatus = registrationStatus;
    }

    public void setCheckInStatus(boolean checkInStatus){
        this.checkInStatus = checkInStatus;
    }

    public void setNotes(String notes){
        this.notes = notes;
    }
    
}
