package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.Map;

import com.example.demo.entity.Event;
import com.example.demo.entity.EventStatus;
import com.example.demo.entity.Participant;
import com.example.demo.entity.Registration;
import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.ParticipantRepository;
import com.example.demo.repository.RegistrationRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class RegistrationIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String staffToken;
    private Event activeEvent;
    private Event cancelledEvent;
    private Participant activeParticipant;
    private Participant inactiveParticipant;

    @BeforeEach
    void setUp() {
        registrationRepository.deleteAll();
        participantRepository.deleteAll();
        eventRepository.deleteAll();
        userRepository.deleteAll();

        User admin = new User(
                "admin",
                "admin@test.com",
                passwordEncoder.encode("admin123"),
                UserRole.ADMIN
        );
        admin.setEnabled(true);
        userRepository.save(admin);

        User staff = new User(
                "staff",
                "staff@test.com",
                passwordEncoder.encode("staff123"),
                UserRole.STAFF
        );
        staff.setEnabled(true);
        userRepository.save(staff);

        activeEvent = new Event(
                "Active Event",
                "Open for registration",
                EventStatus.ACTIVE,
                LocalDateTime.parse("2026-04-15T10:00:00"),
                LocalDateTime.parse("2026-04-15T14:00:00")
        );
        activeEvent = eventRepository.save(activeEvent);

        cancelledEvent = new Event(
                "Cancelled Event",
                "Closed for registration",
                EventStatus.CANCELLED,
                LocalDateTime.parse("2026-04-20T10:00:00"),
                LocalDateTime.parse("2026-04-20T14:00:00")
        );
        cancelledEvent = eventRepository.save(cancelledEvent);

        activeParticipant = Participant.createParticipant(
                "John",
                "Doe",
                "john@test.com",
                "1234567890",
                Participant.Role.ATTENDEE
        );
        activeParticipant = participantRepository.save(activeParticipant);

        inactiveParticipant = Participant.createParticipant(
                "Jane",
                "Smith",
                "jane@test.com",
                "0987654321",
                Participant.Role.ATTENDEE
        );
        inactiveParticipant.setActive(false);
        inactiveParticipant = participantRepository.save(inactiveParticipant);

        adminToken = loginAndGetToken("admin", "admin123");
        staffToken = loginAndGetToken("staff", "staff123");

        assertNotNull(adminToken);
        assertNotNull(staffToken);
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
    @DisplayName("POST /registrations succeeds with real login, token, DB, and HTTP")
    void register_shouldCreateRegistration() {
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/registrations")
                        .queryParam("eventId", activeEvent.getId())
                        .queryParam("participantId", activeParticipant.getParticipantId())
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.registrationStatus").isEqualTo("CONFIRMED")
                .jsonPath("$.checkInStatus").isEqualTo(false);
    }

    @Test
    @DisplayName("POST /registrations returns 404 when event not found")
    void register_shouldReturnNotFound_whenEventMissing() {
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/registrations")
                        .queryParam("eventId", 999999)
                        .queryParam("participantId", activeParticipant.getParticipantId())
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("POST /registrations returns 409 when event is cancelled")
    void register_shouldReturnConflict_whenEventCancelled() {
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/registrations")
                        .queryParam("eventId", cancelledEvent.getId())
                        .queryParam("participantId", activeParticipant.getParticipantId())
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .exchange()
                .expectStatus().isEqualTo(409);
    }

    @Test
    @DisplayName("POST /registrations returns 404 when participant not found")
    void register_shouldReturnNotFound_whenParticipantMissing() {
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/registrations")
                        .queryParam("eventId", activeEvent.getId())
                        .queryParam("participantId", 999999)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("POST /registrations returns 409 when participant is inactive")
    void register_shouldReturnConflict_whenParticipantInactive() {
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/registrations")
                        .queryParam("eventId", activeEvent.getId())
                        .queryParam("participantId", inactiveParticipant.getParticipantId())
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .exchange()
                .expectStatus().isEqualTo(409);
    }

    @Test
    @DisplayName("POST /registrations returns 409 on duplicate registration")
    void register_shouldReturnConflict_whenDuplicate() {
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/registrations")
                        .queryParam("eventId", activeEvent.getId())
                        .queryParam("participantId", activeParticipant.getParticipantId())
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .exchange()
                .expectStatus().isOk();

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/registrations")
                        .queryParam("eventId", activeEvent.getId())
                        .queryParam("participantId", activeParticipant.getParticipantId())
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .exchange()
                .expectStatus().isEqualTo(409);
    }

    @Test
    @DisplayName("POST /registrations returns 403 for STAFF token")
    void register_shouldReturnForbidden_forStaff() {
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/registrations")
                        .queryParam("eventId", activeEvent.getId())
                        .queryParam("participantId", activeParticipant.getParticipantId())
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + staffToken)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @DisplayName("GET /registrations returns all registrations for ADMIN")
    void getAllRegistrations_shouldSucceed_forAdmin() {
        Registration reg = new Registration(
                activeEvent,
                activeParticipant,
                new java.util.Date(),
                com.example.demo.entity.RegistrationStatus.CONFIRMED,
                false,
                "seeded registration"
        );
        registrationRepository.save(reg);

        webTestClient.get()
                .uri("/registrations")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .exchange()
                .expectStatus().isOk();
    }
} 
    

