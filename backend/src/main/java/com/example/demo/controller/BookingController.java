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

    // Organizer + Admin can create a booking
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

    // Organizer + Admin can view all bookings
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    // Organizer + Admin can view a specific booking
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @GetMapping("/{id}")
    public Booking getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id);
    }

    // Organizer + Admin can update requested bookings
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

    // Organizer + Admin can confirm
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @PutMapping("/{id}/confirm")
    public Booking confirmBooking(@PathVariable Long id) {
        return bookingService.confirmBooking(id);
    }

    // Organizer + Admin can cancel
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @PutMapping("/{id}/cancel")
    public Booking cancelBooking(@PathVariable Long id) {
        return bookingService.cancelBooking(id);
    }

    // Organizer + Admin can mark complete
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @PutMapping("/{id}/complete")
    public Booking markComplete(@PathVariable Long id) {
        return bookingService.markComplete(id);
    }
}