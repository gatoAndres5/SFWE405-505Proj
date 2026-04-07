package com.example.demo;

import com.example.demo.entity.Booking;
import com.example.demo.entity.Event;
import com.example.demo.entity.Vendor;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.VendorRepository;
import com.example.demo.service.BookingService;
import com.example.demo.entity.EventStatus;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private VendorRepository vendorRepository;

    @InjectMocks
    private BookingService bookingService;

    public BookingServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateBooking() {

        Long eventId = 1L;
        Long vendorId = 1L;
        String description = "DJ";
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(2);

        Event event = new Event();
        event.setStatus(EventStatus.ACTIVE);
        
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        Vendor vendor = new Vendor();
        vendor.setActive(true);

        when(vendorRepository.findById(vendorId)).thenReturn(Optional.of(vendor));
        when(bookingRepository.save(any())).thenReturn(new Booking());

        Booking result = bookingService.createBooking(
                eventId,
                vendorId,
                description,
                start,
                end
        );

        assertNotNull(result);
    }

    @Test
    void testGetAllBookings() {
        when(bookingRepository.findAll()).thenReturn(List.of(new Booking(), new Booking()));

        List<Booking> result = bookingService.getAllBookings();

        assertEquals(2, result.size());
    }

    @Test
    void testUpdateBooking() {
        Booking existing = new Booking();
        existing.setBookingId(1L);
        existing.setBookingStatus("REQUESTED");

        Vendor vendor = new Vendor();
        vendor.setActive(true);

        Event event = new Event();
        event.setStatus(EventStatus.ACTIVE);

        existing.setVendor(vendor);
        existing.setEvent(event);

        Booking updated = new Booking();
        updated.setServiceDescription("Updated");

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(bookingRepository.save(any())).thenReturn(existing);

        Booking result = bookingService.updateBooking(
                1L,
                "Updated",
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(2)
        );

        assertEquals("Updated", result.getServiceDescription());
    }

    @Test
    void testCancelBooking() {

        Booking booking = new Booking();
        booking.setBookingId(1L);
        booking.setBookingStatus("REQUESTED");

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        when(bookingRepository.save(any())).thenReturn(booking);

        Booking result = bookingService.cancelBooking(1L);

        assertEquals("CANCELLED", result.getBookingStatus());
    }
}