package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.Map;

import com.example.demo.entity.Event;
import com.example.demo.entity.EventStatus;
import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import com.example.demo.repository.EventRepository;
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
class EventIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String staffToken;
    private Event existingEvent;

    @BeforeEach
    void setUp() {
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

        existingEvent = new Event(
                "Existing Event",
                "Already in database",
                EventStatus.DRAFT,
                LocalDateTime.parse("2026-05-10T10:00:00"),
                LocalDateTime.parse("2026-05-10T12:00:00")
        );
        existingEvent = eventRepository.save(existingEvent);

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
    @DisplayName("POST /events creates event for ADMIN")
    void createEvent_shouldSucceed_forAdmin() {
        webTestClient.post()
                .uri("/events")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "name", "Spring Demo Day",
                        "description", "Capstone presentations",
                        "startDateTime", "2026-05-20T13:00:00",
                        "endDateTime", "2026-05-20T15:00:00"
                ))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Spring Demo Day")
                .jsonPath("$.description").isEqualTo("Capstone presentations")
                .jsonPath("$.status").isEqualTo("DRAFT");
    }

    @Test
    @DisplayName("POST /events returns 403 for STAFF")
    void createEvent_shouldReturnForbidden_forStaff() {
        webTestClient.post()
                .uri("/events")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + staffToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "name", "Unauthorized Event",
                        "description", "Should fail",
                        "startDateTime", "2026-05-20T13:00:00",
                        "endDateTime", "2026-05-20T15:00:00"
                ))
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @DisplayName("POST /events returns 400 when required fields are missing")
    void createEvent_shouldReturnBadRequest_whenMissingFields() {
        webTestClient.post()
                .uri("/events")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "name", "",
                        "description", "Missing name",
                        "startDateTime", "2026-05-20T13:00:00",
                        "endDateTime", "2026-05-20T15:00:00"
                ))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("POST /events returns 400 when endDateTime is before startDateTime")
    void createEvent_shouldReturnBadRequest_whenDateRangeInvalid() {
        webTestClient.post()
                .uri("/events")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "name", "Bad Event",
                        "description", "Bad time range",
                        "startDateTime", "2026-05-20T15:00:00",
                        "endDateTime", "2026-05-20T13:00:00"
                ))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("GET /events returns all events for authenticated user")
    void getAllEvents_shouldSucceed_forAuthenticatedUser() {
        webTestClient.get()
                .uri("/events")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("GET /events/{id} returns one event")
    void getEventById_shouldSucceed() {
        webTestClient.get()
                .uri("/events/" + existingEvent.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(existingEvent.getId().intValue())
                .jsonPath("$.name").isEqualTo("Existing Event");
    }

    @Test
    @DisplayName("PUT /events/{id} updates event for ADMIN")
    void updateEvent_shouldSucceed_forAdmin() {
        webTestClient.put()
                .uri("/events/" + existingEvent.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "name", "Updated Event Name",
                        "description", "Updated description",
                        "startDateTime", "2026-05-10T11:00:00",
                        "endDateTime", "2026-05-10T13:00:00"
                ))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Updated Event Name")
                .jsonPath("$.description").isEqualTo("Updated description");
    }

    @Test
    @DisplayName("PUT /events/{id}/cancel sets status to CANCELLED")
    void cancelEvent_shouldSetStatusToCancelled() {
        webTestClient.put()
                .uri("/events/" + existingEvent.getId() + "/cancel")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("CANCELLED");
    }

    @Test
    @DisplayName("GET /events/{id} returns 404 when event not found")
    void getEventById_shouldReturnNotFound_whenMissing() {
        webTestClient.get()
                .uri("/events/999999")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .exchange()
                .expectStatus().isNotFound();
    }
}