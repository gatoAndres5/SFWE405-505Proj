package com.example.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.ScheduleItem;
import java.util.List;

public interface ScheduleItemRepository extends JpaRepository<ScheduleItem, Long>{
    List<ScheduleItem> findByEvent_IdIn(List<Long> eventIds);
}
