package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Participant;
import com.example.demo.repository.ParticipantRepository;

@Service
public class ParticipantService {

    private final ParticipantRepository participantRepository;

    public ParticipantService(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    public Participant createParticipant(Participant participant) {
        Participant newParticipant = Participant.createParticipant(
            participant.getFirstName(),
            participant.getLastName(),
            participant.getEmail(),
            participant.getPhone(),
            participant.getRole()
        );

        return participantRepository.save(newParticipant);
    }

    public List<Participant> getAllParticipants() {
        return participantRepository.findAll();
    }

    public Participant getParticipantById(Long id) {
        return participantRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Participant not found with id: " + id));
    }

    public Participant updateParticipant(Long id, Participant updatedParticipant) {
        Participant existingParticipant = participantRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Participant not found with id: " + id));

        existingParticipant.setFirstName(updatedParticipant.getFirstName());
        existingParticipant.setLastName(updatedParticipant.getLastName());
        existingParticipant.setEmail(updatedParticipant.getEmail());
        existingParticipant.setPhone(updatedParticipant.getPhone());
        existingParticipant.setRole(updatedParticipant.getRole());
        existingParticipant.setActive(updatedParticipant.isActive());

        return participantRepository.save(existingParticipant);
    }

    public void deleteParticipant(Long id) {
        if (!participantRepository.existsById(id)) {
            throw new RuntimeException("Participant not found with id: " + id);
        }

        participantRepository.deleteById(id);
    }

    public Participant deactivateParticipant(Long id) {
        Participant participant = participantRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Participant not found with id: " + id));

        participant.deactivateParticipant();
        return participantRepository.save(participant);
    }
}