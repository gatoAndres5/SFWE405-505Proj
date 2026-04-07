package com.example.demo.repository;
import com.example.demo.entity.Booking;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    boolean existsByVendor_IdAndBookingStatusAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
            Long vendorId,
            String bookingStatus,
            LocalDateTime endDateTime,
            LocalDateTime startDateTime
    );

    boolean existsByVendor_IdAndBookingStatusAndStartDateTimeLessThanAndEndDateTimeGreaterThanAndBookingIdNot(
            Long vendorId,
            String bookingStatus,
            LocalDateTime endDateTime,
            LocalDateTime startDateTime,
            Long bookingId
    );
}