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

import java.util.Date;
import java.util.List;

@Service
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;

    public RegistrationService(RegistrationRepository registrationRepository,
                               EventRepository eventRepository,
                               ParticipantRepository participantRepository) {
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.participantRepository = participantRepository;
    }

    @Transactional
    public Registration register(Long eventId, Long participantId) {

        // 1) Validate Event
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

        // 2) Validate Participant
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

        // 3) Prevent duplicate registration
        boolean alreadyRegistered =
                registrationRepository.existsByEvent_IdAndParticipant_ParticipantId(eventId, participantId);

        if (alreadyRegistered) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Participant is already registered for this event."
            );
        }

        // 4) Create Registration
        Registration registration = new Registration(
                event,
                participant,
                new Date(),
                RegistrationStatus.CONFIRMED,
                false,
                null
        );

        return registrationRepository.save(registration);
    }

    @Transactional(readOnly = true)
    public List<Registration> getAllRegistrations() {
        return registrationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Registration getRegistrationById(Long id) {
        return registrationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Registration not found: " + id
                ));
    }
}