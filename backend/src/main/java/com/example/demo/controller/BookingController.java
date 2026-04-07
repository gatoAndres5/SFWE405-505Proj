package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Booking;
import com.example.demo.service.BookingService;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * Creates a new booking in the system.
     * 
     * @param eventID ID of the event
     * @param vendorID ID of the vendor
     * @param serviceDescription description of the service
     * @param startDateTime booking start time
     * @param endDateTime booking end time
     * @return the created booking
     */
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @PostMapping
    public Booking createBooking(@RequestParam Long eventId,
                                 @RequestParam Long vendorId,
                                 @RequestParam String serviceDescription,
                                 @RequestParam LocalDateTime startDateTime,
                                 @RequestParam LocalDateTime endDateTime) {
        return bookingService.createBooking(
                eventId,
                vendorId,
                serviceDescription,
                startDateTime,
                endDateTime
        );
    }

    /**
     * Retrieves all bookings 
     * 
     * @return list of bookings
     */
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    /**
     * Retrieves a booking by its ID
     * 
     * @param id booking ID
     * @return the requested booking
     */
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @GetMapping("/{id}")
    public Booking getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id);
    }

    /**
     * Updates an existing booking
     * 
     * @param id booking ID
     * @param serviceDescription updated description
     * @param startDateTime updated start time
     * @param endDateTime updated end time
     * @return updated booking
     */
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @PutMapping("/{id}")
    public Booking updateBooking(@PathVariable Long id,
                                 @RequestParam String serviceDescription,
                                 @RequestParam LocalDateTime startDateTime,
                                 @RequestParam LocalDateTime endDateTime) {
        return bookingService.updateBooking(
                id,
                serviceDescription,
                startDateTime,
                endDateTime
        );
    }

    /**
     * Confirms a booking
     * 
     * @param id booking ID
     * @return confirmed booking
     */
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @PutMapping("/{id}/confirm")
    public Booking confirmBooking(@PathVariable Long id) {
        return bookingService.confirmBooking(id);
    }

    /**
     * Deletes a booking by ID
     * 
     * @param id booking ID
     * @return cancelled booking
     */
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @PutMapping("/{id}/cancel")
    public Booking cancelBooking(@PathVariable Long id) {
        return bookingService.cancelBooking(id);
    }

    /**
     * Marks a booking as complete 
     * 
     * @param id booking ID
     * @return completed booking
     */
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @PutMapping("/{id}/complete")
    public Booking markComplete(@PathVariable Long id) {
        return bookingService.markComplete(id);
    }
}