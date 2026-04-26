package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Participant;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long>{
    boolean existsByEmail(String email);
    Optional<Participant> findByEmail(String email);
}
