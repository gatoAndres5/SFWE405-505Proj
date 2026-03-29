package com.example.demo.service;
import com.example.demo.entity.Booking;
import com.example.demo.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    // CREATE
    public Booking createBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    // READ
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    // UPDATE
    public Booking updateBooking(Long id, Booking updatedBooking) {
        Booking booking = bookingRepository.findById(id).orElseThrow();

        booking.setServiceDescription(updatedBooking.getServiceDescription());
        booking.setStartDateTime(updatedBooking.getStartDateTime());
        booking.setEndDateTime(updatedBooking.getEndDateTime());
        booking.setBookingStatus(updatedBooking.getBookingStatus());

        return bookingRepository.save(booking);
    }

    public void cancelBooking(Long id) {
        bookingRepository.deleteById(id);
    }

    public Booking confirmBooking(Long id) {
        Booking booking = bookingRepository.findById(id).orElseThrow();
        booking.setBookingStatus("Confirmed");
        return bookingRepository.save(booking);
    }

    public Booking markComplete(Long id) {
        Booking booking = bookingRepository.findById(id).orElseThrow();
        booking.setBookingStatus("Completed");
        return bookingRepository.save(booking);
    }
}