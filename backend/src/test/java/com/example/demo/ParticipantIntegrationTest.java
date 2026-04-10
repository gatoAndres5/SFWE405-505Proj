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
        participantToken = loginAndGetToken("participant_test", "participant123");

        assertNotNull(adminToken);
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
    @DisplayName("POST /participants creates participant")
    void createParticipant_shouldSucceed() {
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
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.participantId").exists()
            .jsonPath("$.firstName").isEqualTo("Alice")
            .jsonPath("$.lastName").isEqualTo("Smith")
            .jsonPath("$.email").isEqualTo("alice@test.com")
            .jsonPath("$.role").isEqualTo("ATTENDEE")
            .jsonPath("$.active").isEqualTo(true)
            .jsonPath("$.createdAt").exists()
            .jsonPath("$.updatedAt").exists();
    }

    @Test
    @DisplayName("POST /participants returns unauthorized when no token is provided")
    void createParticipant_shouldFail_whenUnauthorized() {
        webTestClient.post()
            .uri("/participants")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Map.of(
                "firstName", "Alice",
                "lastName", "Smith",
                "email", "alice2@test.com",
                "phone", "5551234567",
                "role", "ATTENDEE",
                "active", true
            ))
            .exchange()
            .expectStatus().isForbidden();
    }

    @Test
    @DisplayName("GET /participants returns all participants")
    void getAllParticipants_shouldSucceed() {
        webTestClient.get()
            .uri("/participants")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + participantToken)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Map.class)
            .hasSize(1);
    }

    @Test
    @DisplayName("GET /participants/{id} returns participant by id")
    void getParticipantById_shouldSucceed() {
        webTestClient.get()
            .uri("/participants/{id}", savedParticipant.getParticipantId())
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + participantToken)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.participantId").isEqualTo(savedParticipant.getParticipantId().intValue())
            .jsonPath("$.firstName").isEqualTo("John")
            .jsonPath("$.email").isEqualTo("john@test.com");
    }

    @Test
    @DisplayName("GET /participants/{id} returns not found when participant not found")
    void getParticipantById_shouldFail_whenMissing() {
        webTestClient.get()
            .uri("/participants/{id}", 999999L)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + participantToken)
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("PUT /participants/{id} updates participant")
    void updateParticipant_shouldSucceed() {
        webTestClient.put()
            .uri("/participants/{id}", savedParticipant.getParticipantId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Map.of(
                "firstName", "Johnny",
                "lastName", "Doe",
                "email", "johnny@test.com",
                "phone", "9998887777",
                "role", "SPEAKER",
                "active", true
            ))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.firstName").isEqualTo("Johnny")
            .jsonPath("$.email").isEqualTo("johnny@test.com")
            .jsonPath("$.phone").isEqualTo("9998887777")
            .jsonPath("$.role").isEqualTo("SPEAKER")
            .jsonPath("$.active").isEqualTo(true)
            .jsonPath("$.updatedAt").exists();
    }

    @Test
    @DisplayName("PUT /participants/{id} returns not found when participant not found")
    void updateParticipant_shouldFail_whenMissing() {
        webTestClient.put()
            .uri("/participants/{id}", 999999L)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Map.of(
                "firstName", "Ghost",
                "lastName", "User",
                "email", "ghost@test.com",
                "phone", "0000000000",
                "role", "STAFF",
                "active", true
            ))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("PATCH /participants/{id}/deactivate deactivates participant")
    void deactivateParticipant_shouldSucceed() {
        webTestClient.patch()
            .uri("/participants/{id}/deactivate", savedParticipant.getParticipantId())
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.active").isEqualTo(false)
            .jsonPath("$.updatedAt").exists();
    }

    @Test
    @DisplayName("DELETE /participants/{id} deletes participant")
    void deleteParticipant_shouldSucceed() {
        webTestClient.delete()
            .uri("/participants/{id}", savedParticipant.getParticipantId())
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
            .exchange()
            .expectStatus().isNoContent();
    }

    @Test
    @DisplayName("DELETE /participants/{id} returns not found when participant not found")
    void deleteParticipant_shouldFail_whenMissing() {
        webTestClient.delete()
            .uri("/participants/{id}", 999999L)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("POST /participants returns bad request for duplicate email")
    void createParticipant_shouldFail_whenDuplicateEmail() {
        webTestClient.post()
            .uri("/participants")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Map.of(
                "firstName", "Jane",
                "lastName", "Doe",
                "email", "john@test.com",
                "phone", "4445556666",
                "role", "ATTENDEE",
                "active", true
            ))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("POST /participants returns bad request for invalid blank fields")
    void createParticipant_shouldFail_whenBlankFieldsProvided() {
        webTestClient.post()
            .uri("/participants")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Map.of(
                "firstName", "",
                "lastName", "",
                "email", "",
                "phone", "5550001111",
                "role", "ATTENDEE",
                "active", true
            ))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("POST /participants returns bad request for invalid email format")
    void createParticipant_shouldFail_whenInvalidEmailFormat() {
        webTestClient.post()
            .uri("/participants")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Map.of(
                "firstName", "Invalid",
                "lastName", "Email",
                "email", "not-an-email",
                "phone", "5550002222",
                "role", "ATTENDEE",
                "active", true
            ))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
            .exchange()
            .expectStatus().isBadRequest();
    }
}