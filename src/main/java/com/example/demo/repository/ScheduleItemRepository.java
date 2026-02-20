package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.ScheduleItem;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleItemRepository extends JpaRepository<ScheduleItem, Long> {

    //find all schedule items for an event
    List<ScheduleItem> findByEvent_Id(Long eventId);

    //find all schedule items in a venue
    List<ScheduleItem> findByVenue_Id(Long venueId);

    //find schedule items in time range
    List<ScheduleItem> findByStartDateTimeBetween(LocalDateTime start, LocalDateTime end);

    List<ScheduleItem> findByVenue_IdAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
        Long venueId,
        LocalDateTime end,
        LocalDateTime start);

}

