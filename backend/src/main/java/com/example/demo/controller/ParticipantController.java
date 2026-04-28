package com.example.demo.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
     * Allowed Roles: ADMIN, ORGANIZER
     * 
     * @param participant the participant data to create
     * @return ResponseEntity containing the created participant
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @PostMapping
    public ResponseEntity<Participant> createParticipant(@Valid @RequestBody Participant participant) {
        return ResponseEntity.ok(participantService.createParticipant(participant));
    }

    /**
     * Retrieves all participants.
     * 
     * Allowed Roles: ADMIN, ORGANIZER, STAFF
     * 
     * @return ResponseEntity containing a list of all participants
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER', 'STAFF')")
    @GetMapping
    public ResponseEntity<List<Participant>> getAllParticipants() {
        return ResponseEntity.ok(participantService.getAllParticipants());
    }
    
    //methods for front end use case
    @PreAuthorize("hasRole('PARTICIPANT')")
    @GetMapping("/me")
    public ResponseEntity<Participant> getMyParticipant(Authentication authentication) {
        Participant participant = participantService.getMyParticipant(authentication.getName());
        return ResponseEntity.ok(participant);
    }

    @PreAuthorize("hasRole('PARTICIPANT')")
    @PostMapping("/me")
    public ResponseEntity<Participant> createMyParticipant(
            Authentication authentication,
            @Valid @RequestBody Participant participant) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(participantService.createMyParticipant(authentication.getName(), participant));
    }

    @PreAuthorize("hasRole('PARTICIPANT')")
    @PutMapping("/me")
    public ResponseEntity<Participant> updateMyParticipant(
            Authentication authentication,
            @RequestBody Participant updatedParticipant) {
        return ResponseEntity.ok(
                participantService.updateMyParticipant(authentication.getName(), updatedParticipant)
        );
    }
    
    /**
     * Retrieves a participant by their ID.
     * 
     * Allowed Roles: ADMIN, ORGANIZER, STAFF
     * 
     * @param id the ID of the participant to retrieve
     * @return ResponseEntity containing the requested participant
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER', 'STAFF')")
    @GetMapping("/{id}")
    public ResponseEntity<Participant> getParticipantById(@PathVariable Long id) {
        return ResponseEntity.ok(participantService.getParticipantById(id));
    }

    /**
     * Updates an existing participant.
     * 
     * Allowed Roles: ADMIN, ORGANIZER
     * 
     * @param id the ID of the participant to update
     * @param updatedParticipant the updated participant data
     * @return ResponseEntity containing the updated participant
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @PutMapping("/{id}")
    public ResponseEntity<Participant> updateParticipant(
            @PathVariable Long id,
            @RequestBody Participant updatedParticipant) {
        return ResponseEntity.ok(participantService.updateParticipant(id, updatedParticipant));
    }

    /**
     * Deletes a participant by ID.
     * 
     * Allowed Roles: ADMIN
     * 
     * @param id the ID of the participant to delete
     * @return ResponseEntity with no content if deletion is successful
     */
    @PreAuthorize("hasRole('ADMIN')")
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
     * Allowed Roles: ADMIN, ORGANIZER
     * 
     * @param id the ID of the participant to deactivate
     * @return ResponseEntity containing the updated participant
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Participant> deactivateParticipant(@PathVariable Long id) {
        return ResponseEntity.ok(participantService.deactivateParticipant(id));
    }
}