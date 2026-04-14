package com.example.demo.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Registration;
import com.example.demo.entity.RegistrationStatus;
import com.example.demo.service.RegistrationService;

/**
 * REST controller responsible for handling registration-related operations.
 * 
 * Provides endpoints for registering participants to events, retrieving registrations,
 * updating registration status, and managing check-in/check-out processes.
 * 
 * Security is enforced using role-based authorization.
 */
@RestController
@RequestMapping("/registrations")
public class RegistrationController {

    private final RegistrationService registrationService;

    /**
     * Constructor-based dependency injection for RegistrationService.
     * 
     * @param registrationService the service handling business logic for registrations
     */
    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    /**
     * Registers a participant for an event.
     * 
     * Accessible by ADMIN, ORGANIZER, and PARTICIPANT roles.
     * 
     * @param eventId the ID of the event
     * @param participantId the ID of the participant
     * @param notes the notes of the registration and it is optional
     * @return the created Registration entity
     */
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER','PARTICIPANT')")
    @PostMapping
    public Registration register(@RequestParam Long eventId,
                                 @RequestParam Long participantId,
                                @RequestParam(required = false) String notes) {
        return registrationService.register(eventId, participantId, notes);
    }

    /**
     * Retrieves all registrations in the system.
     * 
     * Accessible by ADMIN, ORGANIZER, and STAFF roles.
     * 
     * @return list of all registrations
     */
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER','STAFF')")
    @GetMapping
    public List<Registration> getAllRegistrations() {
        return registrationService.getAllRegistrations();
    }

    /**
     * Retrieves a specific registration by ID.
     * 
     * Accessible by ADMIN, ORGANIZER, STAFF, and PARTICIPANT roles.
     * 
     * @param id the registration ID
     * @return the requested Registration
     */
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER','STAFF','PARTICIPANT')")
    @GetMapping("/{id}")
    public Registration getRegistrationById(@PathVariable Long id) {
        return registrationService.getRegistrationById(id);
    }

    /**
     * Updates the status of a registration.
     * 
     * Accessible by ADMIN and ORGANIZER roles.
     * 
     * @param id the registration ID
     * @param newStatus the new status to assign
     * @return the updated Registration
     */
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @PutMapping("/{id}/status")
    public Registration updateStatus(@PathVariable Long id,
                                     @RequestParam RegistrationStatus newStatus) {
        return registrationService.updateStatus(id, newStatus);
    }

    /**
     * Cancels a registration.
     * 
     * Accessible by ADMIN and ORGANIZER roles.
     * 
     * @param id the registration ID
     * @return the updated Registration with cancelled status
     */
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @PutMapping("/{id}/cancel")
    public Registration cancelRegistration(@PathVariable Long id) {
        return registrationService.cancelRegistration(id);
    }

    /**
     * Marks a participant as checked in.
     * 
     * Accessible by ADMIN and STAFF roles.
     * 
     * @param id the registration ID
     * @return the updated Registration with check-in status
     */
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @PutMapping("/{id}/checkin")
    public Registration checkIn(@PathVariable Long id) {
        return registrationService.checkIn(id);
    }

    /**
     * Marks a participant as checked out.
     * 
     * Accessible by ADMIN and STAFF roles.
     * 
     * @param id the registration ID
     * @return the updated Registration with check-out status
     */
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @PutMapping("/{id}/checkout")
    public Registration checkOut(@PathVariable Long id) {
        return registrationService.checkOut(id);
    }

    /**
     * Permanently deletes a registration.
     * 
     * Accessible by ADMIN role only.
     * 
     * @param id the registration ID
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteRegistration(@PathVariable Long id) {
        registrationService.deleteRegistration(id);
    }
}