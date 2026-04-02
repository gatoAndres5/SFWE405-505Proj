package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entity.Booking;
import com.example.demo.entity.Event;
import com.example.demo.entity.EventStatus;
import com.example.demo.entity.Vendor;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.VendorRepository;

@Service
public class BookingService {

    private static final String REQUESTED = "REQUESTED";
    private static final String CONFIRMED = "CONFIRMED";
    private static final String CANCELLED = "CANCELLED";
    private static final String COMPLETED = "COMPLETED";

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final VendorRepository vendorRepository;

    public BookingService(BookingRepository bookingRepository,
                          EventRepository eventRepository,
                          VendorRepository vendorRepository) {
        this.bookingRepository = bookingRepository;
        this.eventRepository = eventRepository;
        this.vendorRepository = vendorRepository;
    }

    @Transactional
    public Booking createBooking(Long eventId,
                                 Long vendorId,
                                 String serviceDescription,
                                 LocalDateTime startDateTime,
                                 LocalDateTime endDateTime) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Event not found."
                ));

        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Vendor not found."
                ));

        if (event.getStatus() != EventStatus.ACTIVE) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Event not open for booking."
            );
        }

        if (!vendor.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Vendor is inactive."
            );
        }

        validateTimeRange(startDateTime, endDateTime);

        boolean overlapExists =
                bookingRepository.existsByVendor_IdAndBookingStatusAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
                        vendorId,
                        CONFIRMED,
                        endDateTime,
                        startDateTime
                );

        if (overlapExists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Vendor unavailable for selected time window."
            );
        }

        Booking booking = new Booking();
        booking.setEvent(event);
        booking.setVendor(vendor);
        booking.setServiceDescription(serviceDescription);
        booking.setStartDateTime(startDateTime);
        booking.setEndDateTime(endDateTime);
        booking.setBookingStatus(REQUESTED);

        return bookingRepository.save(booking);
    }

    @Transactional(readOnly = true)
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Booking not found."
                ));
    }

    @Transactional
    public Booking updateBooking(Long id,
                                 String serviceDescription,
                                 LocalDateTime startDateTime,
                                 LocalDateTime endDateTime) {

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Booking not found."
                ));

        if (!REQUESTED.equalsIgnoreCase(booking.getBookingStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only requested bookings may be updated."
            );
        }

        validateTimeRange(startDateTime, endDateTime);

        boolean overlapExists =
                bookingRepository.existsByVendor_IdAndBookingStatusAndStartDateTimeLessThanAndEndDateTimeGreaterThanAndBookingIdNot(
                        booking.getVendor().getId(),
                        CONFIRMED,
                        endDateTime,
                        startDateTime,
                        booking.getBookingId()
                );

        if (overlapExists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Vendor unavailable for selected time window."
            );
        }

        booking.setServiceDescription(serviceDescription);
        booking.setStartDateTime(startDateTime);
        booking.setEndDateTime(endDateTime);

        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking confirmBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Booking not found."
                ));

        if (CANCELLED.equalsIgnoreCase(booking.getBookingStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A cancelled booking may not be re-confirmed."
            );
        }

        if (!REQUESTED.equalsIgnoreCase(booking.getBookingStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only requested bookings may be confirmed."
            );
        }

        if (!booking.getVendor().isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot confirm booking for inactive vendor."
            );
        }

        boolean overlapExists =
                bookingRepository.existsByVendor_IdAndBookingStatusAndStartDateTimeLessThanAndEndDateTimeGreaterThanAndBookingIdNot(
                        booking.getVendor().getId(),
                        CONFIRMED,
                        booking.getEndDateTime(),
                        booking.getStartDateTime(),
                        booking.getBookingId()
                );

        if (overlapExists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Vendor unavailable for selected time window."
            );
        }

        booking.setBookingStatus(CONFIRMED);
        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Booking not found."
                ));

        if (COMPLETED.equalsIgnoreCase(booking.getBookingStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Completed booking cannot be cancelled."
            );
        }

        booking.setBookingStatus(CANCELLED);
        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking markComplete(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Booking not found."
                ));

        if (!CONFIRMED.equalsIgnoreCase(booking.getBookingStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only confirmed bookings may be marked completed."
            );
        }

        if (booking.getEvent().getEndDateTime() == null ||
            booking.getEvent().getEndDateTime().isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Booking cannot be completed before the event ends."
            );
        }

        booking.setBookingStatus(COMPLETED);
        return bookingRepository.save(booking);
    }

    private void validateTimeRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (startDateTime == null || endDateTime == null || !startDateTime.isBefore(endDateTime)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid time range."
            );
        }
    }
}