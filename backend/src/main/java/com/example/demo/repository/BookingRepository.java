package com.example.demo.repository;
import com.example.demo.entity.Booking;

import java.time.LocalDateTime;
import com.example.demo.entity.BookingStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    boolean existsByVendor_IdAndBookingStatusAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
            Long vendorId,
            BookingStatus bookingStatus,
            LocalDateTime endDateTime,
            LocalDateTime startDateTime
    );

    boolean existsByVendor_IdAndBookingStatusAndStartDateTimeLessThanAndEndDateTimeGreaterThanAndBookingIdNot(
            Long vendorId,
            BookingStatus bookingStatus,
            LocalDateTime endDateTime,
            LocalDateTime startDateTime,
            Long bookingId
    );
}