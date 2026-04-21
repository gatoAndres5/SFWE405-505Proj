package com.example.demo.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entity.Event;
import com.example.demo.entity.EventAssignment;
import com.example.demo.entity.Participant;
import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import com.example.demo.repository.EventAssignmentRepository;
import com.example.demo.repository.ParticipantRepository;
import com.example.demo.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EventAssignmentRepository eventAssignmentRepository;
    private final ParticipantRepository participantRepository;

    public UserService(UserRepository userRepository,
                   PasswordEncoder passwordEncoder,
                   EventAssignmentRepository eventAssignmentRepository,
                   ParticipantRepository participantRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventAssignmentRepository = eventAssignmentRepository;
        this.participantRepository = participantRepository;
    }

    @Transactional
    public User createUser(String username, String email, String rawPassword, UserRole role, Long participantId) {
        if (username == null || username.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is required.");
        }

        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required.");
        }

        if (rawPassword == null || rawPassword.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required.");
        }

        if (role == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role is required.");
        }

        if (userRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists.");
        }

        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists.");
        }

        String hashedPassword = passwordEncoder.encode(rawPassword);

        User user = new User(username, email, hashedPassword, role);
        user.setEnabled(true);

        if (role == UserRole.PARTICIPANT) {
            if (participantId == null) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Participant users must be linked to a participant."
                );
            }

            Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Participant not found: " + participantId
                ));

            user.setParticipant(participant);
        } else {
            user.setParticipant(null);
        }

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found: " + id
                ));
    }

    @Transactional(readOnly = true)
    public List<User> getPendingUsers() {
        return userRepository.findByEnabledFalse();
    }

    @Transactional
    public User approveUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found: " + id
                ));

        user.setEnabled(true);
        return userRepository.save(user);
    }

    @Transactional
    public User changeUserRole(Long id, String role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found: " + id
                ));

        if (role == null || role.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role is required.");
        }

        UserRole parsedRole;
        try {
            parsedRole = UserRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role.");
        }

        user.setRole(parsedRole);

        if (parsedRole != UserRole.PARTICIPANT) {
            user.setParticipant(null);
        }

        return userRepository.save(user);
    }

    @Transactional
    public User disableUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found: " + id
                ));

        user.setEnabled(false);
        return userRepository.save(user);
    }

    public List<Event> getAssignedEvents(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User currentUser = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Authenticated user not found: " + username
            ));

        if (currentUser.getRole() == UserRole.ADMIN) {
            return eventAssignmentRepository.findByUser_Id(id).stream()
                .filter(EventAssignment::isActive)
                .map(EventAssignment::getEvent)
                .toList();
        }

        if (currentUser.getRole() == UserRole.ORGANIZER ||
            currentUser.getRole() == UserRole.STAFF) {

            if (!currentUser.getId().equals(id)) {
                throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You may only view your own assigned events."
                );
            }

            return eventAssignmentRepository.findByUser_Id(currentUser.getId()).stream()
                .filter(EventAssignment::isActive)
                .map(EventAssignment::getEvent)
                .toList();
        }

        throw new ResponseStatusException(
            HttpStatus.FORBIDDEN,
            "You are not allowed to view assigned events."
        );
    }
}