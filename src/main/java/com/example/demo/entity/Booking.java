package com.example.demo.entity;



import java.util.Date;

import jakarta.persistence.*;

@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(optional = false)
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    private String serviceDescription;

    private Date startDateTime;

    private Date endDateTime;

    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;

    private Date createdAt;

    private Date updatedAt;

    protected Booking(){}

    public Booking(Event event, Vendor vendor, String serviceDescription, Date startDateTime, Date endDateTime, BookingStatus bookingStatus){
            this.event = event;
            this.vendor = vendor;
            this.serviceDescription = serviceDescription;
            this.startDateTime = startDateTime;
            this.endDateTime = endDateTime;
            this.bookingStatus = bookingStatus;
    }

    //GETTERS

    public Event getEvent(){
        return this.event;
    }

    public Vendor getVendor(){
        return this.vendor;
    }

    public String getServiceDescription(){
        return this.serviceDescription;
    }

    public Date getStartDateTime(){
        return this.startDateTime;
    }

    public Date getEndDateTime(){
        return this.endDateTime;
    }

    public BookingStatus getBookingStatus(){
        return this.bookingStatus;
    }

    public Date getCreatedAt(){
        return this.createdAt;
    }

    public Date getUpdatedAt(){
        return this.updatedAt;
    }

    // SETTERS

    public void setEvent(Event event){
        this.event = event;
    }

    public void setVendor(Vendor vendor){
        this.vendor = vendor;
    }

    public void setServiceDescription(String serviceDesciption){
        this.serviceDescription  = serviceDesciption;
    }

    public void setStartDateTime(Date startDateTime){
        this.startDateTime = startDateTime;
    }

    public void setEndDateTime(Date endDateTime){
        this.endDateTime = endDateTime;
    }

    public void setBookingStatus(BookingStatus bookingStatus){
        this.bookingStatus = bookingStatus;
    }

    public void setCreatedAt(Date createdAt){
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Date updatedAt){
        this.updatedAt = updatedAt;
    }
}
