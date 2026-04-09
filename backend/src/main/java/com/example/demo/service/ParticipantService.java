package com.example.demo.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entity.Participant;
import com.example.demo.repository.ParticipantRepository;

@Service
public class ParticipantService {

    private final ParticipantRepository participantRepository;

    public ParticipantService(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    public Participant createParticipant(Participant participant) {
        if (participant == null) {
            throw new IllegalArgumentException("Participant data is required");
        }

        String firstName = participant.getFirstName();
        String lastName = participant.getLastName();
        String email = participant.getEmail();
        String phone = participant.getPhone();
        Participant.Role role = participant.getRole();

        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name is required");
        }

        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name is required");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        email = email.trim();

        if (participantRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("A participant with this email already exists");
        }

        Participant newParticipant = Participant.createParticipant(
            firstName,
            lastName,
            email,
            phone,
            role
        );

        return participantRepository.save(newParticipant);
    }

    public List<Participant> getAllParticipants() {
        return participantRepository.findAll();
    }

    public Participant getParticipantById(Long id) {
        return participantRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Participant not found with id: " + id
            ));
    }

    public Participant updateParticipant(Long id, Participant updatedParticipant) {
        Participant existingParticipant = participantRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Participant not found with id: " + id
        ));

        existingParticipant.setFirstName(updatedParticipant.getFirstName());
        existingParticipant.setLastName(updatedParticipant.getLastName());
        existingParticipant.setEmail(updatedParticipant.getEmail());
        existingParticipant.setPhone(updatedParticipant.getPhone());
        existingParticipant.setRole(updatedParticipant.getRole());
        existingParticipant.setActive(updatedParticipant.isActive());

        return participantRepository.save(existingParticipant);
    }

    public void deleteParticipant(Long id) {
        Participant participant = participantRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Participant not found with id: " + id
        ));

        participantRepository.delete(participant);
    }

    public Participant deactivateParticipant(Long id) {
        Participant participant = participantRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Participant not found with id: " + id));

        participant.deactivateParticipant();
        return participantRepository.save(participant);
    }
}