package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Registration;
import java.util.List;

public interface RegistrationRepository extends JpaRepository<Registration, Long>{
    boolean existsByEvent_IdAndParticipant_ParticipantId(Long eventId, Long participantId);
    List<Registration> findByEvent_IdIn(List<Long> eventIds);

    List<Registration> findByParticipant_ParticipantId(Long participantId);
}
