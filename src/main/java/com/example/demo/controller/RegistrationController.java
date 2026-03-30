package com.example.demo.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Registration;
import com.example.demo.entity.RegistrationStatus;
import com.example.demo.service.RegistrationService;

@RestController
@RequestMapping("/registrations")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    // Participant OR Organizer OR Admin can register
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER','PARTICIPANT')")
    @PostMapping
    public Registration register(@RequestParam Long eventId,
                                 @RequestParam Long participantId) {
        return registrationService.register(eventId, participantId);
    }

    // Organizer + Staff + Admin can view all
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER','STAFF')")
    @GetMapping
    public List<Registration> getAllRegistrations() {
        return registrationService.getAllRegistrations();
    }

    // Everyone can view specific (optional, you can restrict more later)
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER','STAFF','PARTICIPANT')")
    @GetMapping("/{id}")
    public Registration getRegistrationById(@PathVariable Long id) {
        return registrationService.getRegistrationById(id);
    }

    // Only Organizer + Admin can update status
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @PutMapping("/{id}/status")
    public Registration updateStatus(@PathVariable Long id,
                                     @RequestParam RegistrationStatus newStatus) {
        return registrationService.updateStatus(id, newStatus);
    }

    // Only Organizer + Admin can cancel
    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
    @PutMapping("/{id}/cancel")
    public Registration cancelRegistration(@PathVariable Long id) {
        return registrationService.cancelRegistration(id);
    }

    // Only Staff + Admin can check in
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @PutMapping("/{id}/checkin")
    public Registration checkIn(@PathVariable Long id) {
        return registrationService.checkIn(id);
    }

    // Only Staff + Admin can check out
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @PutMapping("/{id}/checkout")
    public Registration checkOut(@PathVariable Long id) {
        return registrationService.checkOut(id);
    }

    // Only Admin can delete permanently
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteRegistration(@PathVariable Long id) {
        registrationService.deleteRegistration(id);
    }
}