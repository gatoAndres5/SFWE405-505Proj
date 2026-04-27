package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;

import com.example.demo.entity.Participant;
import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import com.example.demo.repository.ParticipantRepository;
import com.example.demo.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ParticipantIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String organizerToken;
    private String staffToken;
    private String participantToken;

    private Participant savedParticipant;

    private Participant createTestParticipant() {
        Participant participant = Participant.createParticipant(
            "John",
            "Doe",
            "john@test.com",
            "1234567890",
            Participant.Role.ATTENDEE
        );
        return participantRepository.save(participant);
    }

    @BeforeEach
    void setUp() {
        participantRepository.deleteAll();
        userRepository.deleteAll();

        User admin = new User(
            "admin_test",
            "admin@test.com",
            passwordEncoder.encode("admin123"),
            UserRole.ADMIN
        );
        admin.setEnabled(true);
        userRepository.save(admin);

        User organizer = new User(
            "organizer_test",
            "organizer@test.com",
            passwordEncoder.encode("organizer123"),
            UserRole.ORGANIZER
        );
        organizer.setEnabled(true);
        userRepository.save(organizer);

        User staff = new User(
            "staff_test",
            "staff@test.com",
            passwordEncoder.encode("staff123"),
            UserRole.STAFF
        );
        staff.setEnabled(true);
        userRepository.save(staff);

        User participantUser = new User(
            "participant_test",
            "participant@test.com",
            passwordEncoder.encode("participant123"),
            UserRole.PARTICIPANT
        );
        participantUser.setEnabled(true);
        userRepository.save(participantUser);

        savedParticipant = createTestParticipant();

        adminToken = loginAndGetToken("admin_test", "admin123");
        organizerToken = loginAndGetToken("organizer_test", "organizer123");
        staffToken = loginAndGetToken("staff_test", "staff123");
        participantToken = loginAndGetToken("participant_test", "participant123");

        assertNotNull(adminToken);
        assertNotNull(organizerToken);
        assertNotNull(staffToken);
        assertNotNull(participantToken);
    }

    @SuppressWarnings("unchecked")
    private String loginAndGetToken(String username, String password) {
        Map<String, Object> response = webTestClient.post()
            .uri("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Map.of(
                "username", username,
                "password", password
            ))
            .exchange()
            .expectStatus().isOk()
            .expectBody(Map.class)
            .returnResult()
            .getResponseBody();

        return response.get("token").toString();
    }

    @Test
    @DisplayName("POST /participants succeeds for admin")
    void createParticipant_shouldSucceed_forAdmin() {
        webTestClient.post()
            .uri("/participants")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Map.of(
                "firstName", "Alice",
                "lastName", "Smith",
                "email", "alice@test.com",
                "phone", "5551234567",
                "role", "ATTENDEE",
                "active", true
            ))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    @DisplayName("POST /participants fails for participant user")
    void createParticipant_shouldFail_forParticipantUser() {
        webTestClient.post()
            .uri("/participants")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Map.of(
                "firstName", "Bob",
                "lastName", "User",
                "email", "bob@test.com",
                "phone", "5551234567",
                "role", "ATTENDEE",
                "active", true
            ))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + participantToken)
            .exchange()
            .expectStatus().isForbidden();
    }

    @Test
    @DisplayName("GET /participants succeeds for admin")
    void getAllParticipants_shouldSucceed_forAdmin() {
        webTestClient.get()
            .uri("/participants")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    @DisplayName("GET /participants fails for participant user")
    void getAllParticipants_shouldFail_forParticipantUser() {
        webTestClient.get()
            .uri("/participants")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + participantToken)
            .exchange()
            .expectStatus().isForbidden();
    }

    @Test
    @DisplayName("PUT /participants/{id} succeeds for admin")
    void updateParticipant_shouldSucceed_forAdmin() {
        webTestClient.put()
            .uri("/participants/{id}", savedParticipant.getParticipantId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Map.of(
                "firstName", "Updated",
                "lastName", "User",
                "email", "updated@test.com",
                "phone", "9998887777",
                "role", "SPEAKER",
                "active", true
            ))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    @DisplayName("PUT /participants/{id} fails for staff")
    void updateParticipant_shouldFail_forStaff() {
        webTestClient.put()
            .uri("/participants/{id}", savedParticipant.getParticipantId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Map.of(
                "firstName", "Blocked",
                "lastName", "Staff",
                "email", "blocked@test.com",
                "phone", "1231231234",
                "role", "STAFF",
                "active", true
            ))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + staffToken)
            .exchange()
            .expectStatus().isForbidden();
    }

    @Test
    @DisplayName("DELETE /participants/{id} succeeds for admin")
    void deleteParticipant_shouldSucceed_forAdmin() {
        webTestClient.delete()
            .uri("/participants/{id}", savedParticipant.getParticipantId())
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
            .exchange()
            .expectStatus().isNoContent();
    }

    @Test
    @DisplayName("DELETE /participants/{id} fails for organizer")
    void deleteParticipant_shouldFail_forOrganizer() {
        webTestClient.delete()
            .uri("/participants/{id}", savedParticipant.getParticipantId())
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + organizerToken)
            .exchange()
            .expectStatus().isForbidden();
    }

    @Test
    @DisplayName("PATCH /participants/{id}/deactivate succeeds for admin")
    void deactivateParticipant_shouldSucceed_forAdmin() {
        webTestClient.patch()
            .uri("/participants/{id}/deactivate", savedParticipant.getParticipantId())
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    @DisplayName("PATCH /participants/{id}/deactivate fails for staff")
    void deactivateParticipant_shouldFail_forStaff() {
        webTestClient.patch()
            .uri("/participants/{id}/deactivate", savedParticipant.getParticipantId())
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + staffToken)
            .exchange()
            .expectStatus().isForbidden();
    }
}