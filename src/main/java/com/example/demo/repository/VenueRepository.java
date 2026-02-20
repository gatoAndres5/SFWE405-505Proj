package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Venue;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long>{
    
    List<Venue> findByNameContaining(String name);
    
    List<Venue> findByCapacityGreaterThanEqual(int capacity);
    
    @Query("SELECT v FROM Venue v WHERE v.venueId NOT IN " +
           "(SELECT s.venue.venueId FROM ScheduleItem s WHERE " +
           "s.startTime < :endTime AND s.endTime > :startTime)")
    List<Venue> findAvailableVenues(@Param("startTime") LocalDateTime startTime, 
                                   @Param("endTime") LocalDateTime endTime);
}
