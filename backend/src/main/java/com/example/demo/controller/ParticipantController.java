package com.example.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Participant;
import com.example.demo.service.ParticipantService;

@RestController
@RequestMapping("/participants")
public class ParticipantController {

    private final ParticipantService participantService;

    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    @PostMapping
    public ResponseEntity<Participant> createParticipant(@RequestBody Participant participant) {
        return ResponseEntity.ok(participantService.createParticipant(participant));
    }

    @GetMapping
    public ResponseEntity<List<Participant>> getAllParticipants() {
        return ResponseEntity.ok(participantService.getAllParticipants());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Participant> getParticipantById(@PathVariable Long id) {
        return ResponseEntity.ok(participantService.getParticipantById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Participant> updateParticipant(
            @PathVariable Long id,
            @RequestBody Participant updatedParticipant) {
        return ResponseEntity.ok(participantService.updateParticipant(id, updatedParticipant));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParticipant(@PathVariable Long id) {
        participantService.deleteParticipant(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Participant> deactivateParticipant(@PathVariable Long id) {
        return ResponseEntity.ok(participantService.deactivateParticipant(id));
    }
}