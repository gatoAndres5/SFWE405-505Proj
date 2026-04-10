package com.example.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Participant;
import com.example.demo.service.ParticipantService;

import jakarta.validation.Valid;

/**
 * REST controller for managing Participant entities.
 * 
 * Provides endpoints for creating, retrieving, updating, deleting,
 * and deactivating participants in the system.
 */
@RestController
@RequestMapping("/participants")
public class ParticipantController {

    private final ParticipantService participantService;

    /**
     * Constructs a ParticipantController with the required service.
     * 
     * @param participantService service layer for participant operations
     */
    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    /**
     * Creates a new participant.
     * 
     * Validates the incoming participant data and persists it to the database.
     * 
     * @param participant the participant data to create
     * @return ResponseEntity containing the created participant
     */
    @PostMapping
    public ResponseEntity<Participant> createParticipant(@Valid @RequestBody Participant participant) {
        return ResponseEntity.ok(participantService.createParticipant(participant));
    }

    /**
     * Retrieves all participants.
     * 
     * @return ResponseEntity containing a list of all participants
     */
    @GetMapping
    public ResponseEntity<List<Participant>> getAllParticipants() {
        return ResponseEntity.ok(participantService.getAllParticipants());
    }

    /**
     * Retrieves a participant by their ID.
     * 
     * @param id the ID of the participant to retrieve
     * @return ResponseEntity containing the requested participant
     */
    @GetMapping("/{id}")
    public ResponseEntity<Participant> getParticipantById(@PathVariable Long id) {
        return ResponseEntity.ok(participantService.getParticipantById(id));
    }

    /**
     * Updates an existing participant.
     * 
     * @param id the ID of the participant to update
     * @param updatedParticipant the updated participant data
     * @return ResponseEntity containing the updated participant
     */
    @PutMapping("/{id}")
    public ResponseEntity<Participant> updateParticipant(
            @PathVariable Long id,
            @RequestBody Participant updatedParticipant) {
        return ResponseEntity.ok(participantService.updateParticipant(id, updatedParticipant));
    }

    /**
     * Deletes a participant by ID.
     * 
     * @param id the ID of the participant to delete
     * @return ResponseEntity with no content if deletion is successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParticipant(@PathVariable Long id) {
        participantService.deleteParticipant(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deactivates a participant.
     * 
     * Sets the participant's active status to false without deleting the record.
     * 
     * @param id the ID of the participant to deactivate
     * @return ResponseEntity containing the updated participant
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Participant> deactivateParticipant(@PathVariable Long id) {
        return ResponseEntity.ok(participantService.deactivateParticipant(id));
    }
}