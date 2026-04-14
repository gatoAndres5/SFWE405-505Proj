package com.example.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.EventAssignment;
import java.util.List;

public interface EventAssignmentRepository extends JpaRepository<EventAssignment, Long> {
    List<EventAssignment> findByEventId(Long eventId);
    List<EventAssignment> findByUser_Id(Long userId);
}
