package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Participant;
import com.example.demo.repository.ParticipantRepository;

@RestController
@RequestMapping("/participants")
public class ParticipantController {

    @Autowired
    private ParticipantRepository participantRepository;

    @PostMapping
    public Participant createParticipant(@RequestBody Participant participant) {
        return participantRepository.save(participant);
    }

    @GetMapping
    public List<Participant> getAllParticipants() {
        return participantRepository.findAll();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParticipant(@PathVariable Long id) {

        if (!participantRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        participantRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Participant> updateParticipant(
            @PathVariable Long id,
            @RequestBody Participant updatedParticipant) {

        return participantRepository.findById(id)
                .map(existingParticipant -> {

                    existingParticipant.setFirstName(updatedParticipant.getFirstName());
                    existingParticipant.setLastName(updatedParticipant.getLastName());
                    existingParticipant.setEmail(updatedParticipant.getEmail());
                    existingParticipant.setPhone(updatedParticipant.getPhone());
                    existingParticipant.setRole(updatedParticipant.getRole());
                    existingParticipant.setActive(updatedParticipant.isActive());

                    Participant saved = participantRepository.save(existingParticipant);
                    return ResponseEntity.ok(saved);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
