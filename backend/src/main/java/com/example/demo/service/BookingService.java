package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entity.Booking;
import com.example.demo.entity.Event;
import com.example.demo.entity.EventAssignment;
import com.example.demo.entity.EventStatus;
import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import com.example.demo.entity.Vendor;
import com.example.demo.entity.BookingStatus;
import com.example.demo.entity.Participant;

import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.EventAssignmentRepository;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.VendorRepository;
import com.example.demo.repository.RegistrationRepository;


@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final VendorRepository vendorRepository;
    private final UserRepository userRepository;
    private final EventAssignmentRepository eventAssignmentRepository;
    private final RegistrationRepository registrationRepository;

    public BookingService(
                BookingRepository bookingRepository,
                EventRepository eventRepository,
                VendorRepository vendorRepository,
                UserRepository userRepository,
                EventAssignmentRepository eventAssignmentRepository,
                RegistrationRepository registrationRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.eventRepository = eventRepository;
        this.vendorRepository = vendorRepository;
        this.userRepository = userRepository;
        this.eventAssignmentRepository = eventAssignmentRepository;
        this.registrationRepository = registrationRepository;
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
                        BookingStatus.CONFIRMED,
                        endDateTime,
                        startDateTime
                );

        if (overlapExists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Vendor unavailable for selected time window."
            );
        }

        Booking booking = new Booking(
                event,
                vendor,
                serviceDescription,
                startDateTime,
                endDateTime
        );

        booking.setBookingStatus(BookingStatus.REQUESTED);

        return bookingRepository.save(booking);
    }

    @Transactional(readOnly = true)
        public List<Booking> getAllBookings() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Authenticated user not found: " + username
                ));

        // ADMIN → sees all bookings
        if (currentUser.getRole() == UserRole.ADMIN) {
                return bookingRepository.findAll();
        }

        // ORGANIZER / STAFF → see bookings for assigned events
        if (currentUser.getRole() == UserRole.ORGANIZER ||
                currentUser.getRole() == UserRole.STAFF) {

                List<Long> eventIds = eventAssignmentRepository.findByUser_Id(currentUser.getId())
                        .stream()
                        .filter(EventAssignment::isActive)
                        .map(a -> a.getEvent().getId())
                        .toList();

                return bookingRepository.findByEvent_IdIn(eventIds);
        }

        // PARTICIPANT → see bookings for events they are registered for
        if (currentUser.getRole() == UserRole.PARTICIPANT) {

                Participant participant = currentUser.getParticipant();

                if (participant == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "No participant profile linked to this user."
                );
                }

                List<Long> eventIds = registrationRepository
                        .findByParticipant_ParticipantId(participant.getParticipantId())
                        .stream()
                        .map(r -> r.getEvent().getId())
                        .distinct()
                        .toList();

                return bookingRepository.findByEvent_IdIn(eventIds);
        }

        return List.of();
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

        if (booking.getBookingStatus() != BookingStatus.REQUESTED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only requested bookings may be updated."
            );
        }

        validateTimeRange(startDateTime, endDateTime);

        boolean overlapExists =
                bookingRepository.existsByVendor_IdAndBookingStatusAndStartDateTimeLessThanAndEndDateTimeGreaterThanAndBookingIdNot(
                        booking.getVendor().getId(),
                        BookingStatus.CONFIRMED,
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

        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A cancelled booking may not be re-confirmed."
            );
        }

        if (booking.getBookingStatus() != BookingStatus.REQUESTED) {
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
                        BookingStatus.CONFIRMED,
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

        booking.setBookingStatus(BookingStatus.CONFIRMED);
        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Booking not found."
                ));

        if (booking.getBookingStatus() == BookingStatus.COMPLETED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Completed booking cannot be cancelled."
            );
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking markComplete(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Booking not found."
                ));

        if (booking.getBookingStatus() != BookingStatus.CONFIRMED) {
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

        booking.setBookingStatus(BookingStatus.COMPLETED);
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