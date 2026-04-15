package com.example.demo.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entity.Event;
import com.example.demo.entity.EventStatus;
import com.example.demo.entity.Participant;
import com.example.demo.entity.Registration;
import com.example.demo.entity.RegistrationStatus;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.ParticipantRepository;
import com.example.demo.repository.RegistrationRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.demo.entity.EventAssignment;
import com.example.demo.entity.Participant;
import com.example.demo.entity.Registration;
import com.example.demo.entity.RegistrationStatus;
import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import com.example.demo.repository.EventAssignmentRepository;
import com.example.demo.repository.UserRepository;

import java.util.Date;
import java.util.List;

@Service
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final EventAssignmentRepository eventAssignmentRepository;

    public RegistrationService(RegistrationRepository registrationRepository,
                           EventRepository eventRepository,
                           ParticipantRepository participantRepository,
                           UserRepository userRepository,
                           EventAssignmentRepository eventAssignmentRepository) {
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.participantRepository = participantRepository;
        this.userRepository = userRepository;
        this.eventAssignmentRepository = eventAssignmentRepository;
    }

    @Transactional
    public Registration register(Long eventId, Long participantId, String notes) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Event not found: " + eventId
                ));

        if (event.getStatus() == EventStatus.CANCELLED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Event is not open for registration."
            );
        }

        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Participant not found: " + participantId
                ));

        if (!participant.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Participant is inactive and cannot register."
            );
        }

        boolean alreadyRegistered =
                registrationRepository.existsByEvent_IdAndParticipant_ParticipantId(eventId, participantId);

        if (alreadyRegistered) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Participant is already registered for this event."
            );
        }

        Registration registration = new Registration(
                event,
                participant,
                new Date(),
                RegistrationStatus.PENDING,
                false,
                notes
        );

        return registrationRepository.save(registration);
    }

    @Transactional(readOnly = true)
    public List<Registration> getAllRegistrations() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Authenticated user not found: " + username
                ));

        // ADMIN → sees everything
        if (currentUser.getRole() == UserRole.ADMIN) {
                return registrationRepository.findAll();
        }

        // ORGANIZER or STAFF → see registrations for assigned events
        if (currentUser.getRole() == UserRole.ORGANIZER ||
                currentUser.getRole() == UserRole.STAFF) {

                List<EventAssignment> assignments =
                eventAssignmentRepository.findByUser_Id(currentUser.getId());

                List<Long> eventIds = assignments.stream()
                .filter(a -> a.isActive())
                .map(a -> a.getEvent().getId())
                .toList();

                return registrationRepository.findByEvent_IdIn(eventIds);
        }

        return List.of();
     }

    @Transactional(readOnly = true)
    public Registration getRegistrationById(Long id) {
        return registrationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Registration not found: " + id
                ));
    }

    @Transactional
    public Registration updateStatus(Long id, RegistrationStatus newStatus) {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Registration not found: " + id
                ));

        registration.setRegistrationStatus(newStatus);
        return registrationRepository.save(registration);
    }

    @Transactional
    public Registration cancelRegistration(Long id) {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Registration not found: " + id
                ));

        registration.setRegistrationStatus(RegistrationStatus.CANCELLED);
        registration.setCheckInStatus(false);
        return registrationRepository.save(registration);
    }

    @Transactional
    public Registration checkIn(Long id) {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Registration not found: " + id
                ));

        if (registration.getRegistrationStatus() != RegistrationStatus.CONFIRMED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only confirmed registrations may be checked in."
            );
        }

        if (registration.getCheckInStatus()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Participant is already checked in."
            );
        }

        registration.setCheckInStatus(true);
        return registrationRepository.save(registration);
    }

    @Transactional
    public Registration checkOut(Long id) {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Registration not found: " + id
                ));

        if (!registration.getCheckInStatus()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Participant is not currently checked in."
            );
        }

        registration.setCheckInStatus(false);
        return registrationRepository.save(registration);
    }

    @Transactional
    public void deleteRegistration(Long id) {
        if (!registrationRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Registration not found: " + id
            );
        }

        registrationRepository.deleteById(id);
    }
}