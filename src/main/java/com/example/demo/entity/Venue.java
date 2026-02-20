package com.example.demo.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
public class Venue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long venueId;

    @ManyToMany
    @JoinTable(name = "event_venue",
    joinColumns = @JoinColumn(name = "venue_id"),
    inverseJoinColumns = @JoinColumn(name = "event_id"))
    private List<Event> events = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "venue_id")
    private List<ScheduleItem> scheduleItems = new ArrayList<>();

    private String name;
    private String address;
    private int capacity;
    private String contactName;
    private String contactEmail;
    private String contactPhone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    //CONSTRUCTORS
    public Venue() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Venue(String name, String address, int capacity, 
        String contactName, String contactEmail, String contactPhone) {
            this.name = name;
            this.address = address;
            this.capacity = capacity;
            this.contactName = contactName;
            this.contactEmail = contactEmail;
            this.contactPhone = contactPhone;
            this.createdAt = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();
    }

    //GETTERS
    public Long getVenueId() {
        return venueId;
    }
    
    public List<Event> getEvents() {
        return events;
    }
    
    public List<ScheduleItem> getScheduleItems() {
        return scheduleItems;
    }
    public String getName() {
        return name;
    }
    public String getAddress(){
        return address;
    }
    public int getCapacity(){
        return capacity;
    }
    public String getContactName(){
        return contactName;
    }
    public String getContactEmail(){
        return contactEmail;
    }
    public String getContactPhone(){
        return contactPhone;
    }
    public LocalDateTime getCreatedAt(){
        return createdAt;
    }
    public LocalDateTime getUpdatedAt(){
        return updatedAt;
    }    

    //SETTERS
    public void setVenueId(Long venueId) {
        this.venueId = venueId;
    }
    
    public void setEvents(List<Event> events){
        this.events = events;
    }
    
    public void setScheduleItems(List<ScheduleItem> scheduleItems){
        this.scheduleItems = scheduleItems;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setAddress(String address){
        this.address = address;
    }
    public void setCapacity(int capacity){
        this.capacity = capacity;
    }
    public void setContactName(String contactName){
        this.contactName = contactName;
    }
    public void setContactEmail(String contactEmail){
        this.contactEmail = contactEmail;
    }
    public void setContactPhone(String contactPhone){
        this.contactPhone = contactPhone;
    }
    public void setCreatedAt(LocalDateTime createdAt){
        this.createdAt = createdAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt){
        this.updatedAt = updatedAt;
    }
}
