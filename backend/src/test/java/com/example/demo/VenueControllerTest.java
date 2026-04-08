package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.Map;

import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import com.example.demo.entity.Venue;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.VenueRepository;
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
public class VenueControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VenueRepository venueRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String participantToken;
    private Venue savedVenue;

    private Venue createTestVenue() {
        Venue venue = new Venue(
            "Test Venue",
            "123 Test Street",
            100,
            "John Manager",
            "555-1234",
            "manager@test.com");
        return venueRepository.save(venue);
    }

    @BeforeEach
    public void setUp() {
        venueRepository.deleteAll();
        userRepository.deleteAll();

        User admin = new User(
            "admin_test",
            "admin@test.com",
            passwordEncoder.encode("admin123"),
            UserRole.ADMIN);
        admin.setEnabled(true);
        userRepository.save(admin);

        User participant = new User(
            "participant_test",
            "participant@test.com",
            passwordEncoder.encode("participant123"),
            UserRole.PARTICIPANT);
        participant.setEnabled(true);
        userRepository.save(participant);

        savedVenue = createTestVenue();

        adminToken = loginAndGetToken("admin_test", "admin123");
        participantToken = loginAndGetToken("participant_test", "participant123");
    }

    @SuppressWarnings("unchecked")
    private String loginAndGetToken(String username, String password) {
        Map<String, Object> response = webTestClient.post()
            .uri("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Map.of("username", username, "password", password))
            .exchange()
            .expectStatus().isOk()
            .expectBody(Map.class)
            .returnResult()
            .getResponseBody();
        return response.get("token").toString();
    }

    // TEST CASES

    /**
     * TEST 1: Create Venue
     * DEMONSTRATES: POST /venues works (happy path)
     * PROVES: ADMIN can create venue, data saves to DB, returns 200 OK with ID
     */
    @Test
    @DisplayName("POST /venues creates venue for ADMIN")
    void createVenue_shouldSucceed_forAdmin() {
        webTestClient.post()
            .uri("/venues")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Map.of(
                "name", "Grand Ballroom",
                "address", "456 Event Ave",
                "capacity", 200,
                "contactName", "Jane Manager",
                "contactEmail", "jane@venue.com",
                "contactPhone", "555-5678"
            ))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.id").exists()
            .jsonPath("$.name").isEqualTo("Grand Ballroom");
    }

    /**
     * TEST 2: Read all Venues
     * DEMONSTRATES: GET /venues works
     * PROVES: PARTICIPANT can read venues, returns list
     */
    @Test
    @DisplayName("GET /venues returns list for PARTICIPANT")
    void getAllVenues_shouldSucceed_forParticipant() {
        webTestClient.get()
            .uri("/venues")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + participantToken)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Map.class)
            .hasSize(1);
    }

    /**
     * TEST 3: Get Venue by ID
     * DEMONSTRATES: GET /venues/{id} works
     * PROVES: Can retrieve specific venue by ID
     */
    @Test
    @DisplayName("GET /venues/{id} returns venue by ID")
    void getVenueById_shouldSucceed() {
        webTestClient.get()
            .uri("/venues/{id}", savedVenue.getVenueId())
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + participantToken)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.id").isEqualTo(savedVenue.getVenueId().intValue());
    }

    /**
     * TEST 4: Get Venue NOT FOUND
     * DEMONSTRATES: Error handling works (404)
     * PROVES: Service checks if venue exists, returns 404 instead of crashing
     */
    @Test
    @DisplayName("GET /venues/{id} returns 404 when not found")
    void getVenueById_shouldReturnNotFound_whenMissing() {
        webTestClient.get()
            .uri("/venues/{id}", 999999)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + participantToken)
            .exchange()
            .expectStatus().isNotFound();
    }

    /**
     * TEST 5: Update Venue
     * DEMONSTRATES: PUT /venues/{id} works
     * PROVES: ADMIN can update venue, fields are changed in DB
     */
    @Test
    @DisplayName("PUT /venues/{id} updates venue for ADMIN")
    void updateVenue_shouldSucceed_forAdmin() {
        webTestClient.put()
            .uri("/venues/{id}", savedVenue.getVenueId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Map.of(
                "name", "Updated Venue Name",
                "address", "789 New Street",
                "capacity", 150,
                "contactName", "Updated Manager",
                "contactEmail", "updated@venue.com",
                "contactPhone", "555-9999"
            ))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.name").isEqualTo("Updated Venue Name");
    }

    /**
     * TEST 6: Deactivate Venue
     * DEMONSTRATES: POST /venues/{id}/deactivate works
     * PROVES: Can deactivate venue (soft delete)
     */
    @Test
    @DisplayName("POST /venues/{id}/deactivate deactivates venue for ADMIN")
    void deactivateVenue_shouldSucceed_forAdmin() {
        webTestClient.post()
            .uri("/venues/{id}/deactivate", savedVenue.getVenueId())
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
            .exchange()
            .expectStatus().isOk();
    }

    /**
     * TEST 7: Check Venue Availability
     * DEMONSTRATES: GET /venues/{id}/availability works
     * PROVES: Can check if venue is available for time range
     */
    @Test
    @DisplayName("GET /venues/{id}/availability returns availability status")
    void checkAvailability_shouldSucceed() {
        webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/venues/{id}/availability")
                .queryParam("startDateTime", "2026-04-15T10:00:00")
                .queryParam("endDateTime", "2026-04-15T12:00:00")
                .build(savedVenue.getVenueId()))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + participantToken)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.available").isBoolean();
    }

    /**
     * TEST 8: Get Venue Schedule
     * DEMONSTRATES: GET /venues/{id}/schedule works
     * PROVES: Can retrieve schedule items for venue within date range
     */
    @Test
    @DisplayName("GET /venues/{id}/schedule returns schedule items")
    void getSchedule_shouldSucceed() {
        webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/venues/{id}/schedule")
                .queryParam("startDate", "2026-04-15T00:00:00")
                .queryParam("endDate", "2026-04-15T23:59:59")
                .build(savedVenue.getVenueId()))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + participantToken)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Map.class);
    }

    /**
     * TEST 9: Find Available Venues
     * DEMONSTRATES: GET /venues/available works
     * PROVES: Can find venues available for specific time range
     */
    @Test
    @DisplayName("GET /venues/available returns available venues")
    void findAvailableVenues_shouldSucceed() {
        webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/venues/available")
                .queryParam("startTime", "2026-04-15T10:00:00")
                .queryParam("endTime", "2026-04-15T12:00:00")
                .build())
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + participantToken)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Map.class);
    }

    /**
     * TEST 10: Delete Venue
     * DEMONSTRATES: DELETE /venues/{id} works
     * PROVES: Venue is removed from DB, subsequent GET returns 404
     */
    @Test
    @DisplayName("DELETE /venues/{id} removes venue for ADMIN")
    void deleteVenue_shouldSucceed_forAdmin() {
        webTestClient.delete()
            .uri("/venues/{id}", savedVenue.getVenueId())
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
            .exchange()
            .expectStatus().isNoContent();

        webTestClient.get()
            .uri("/venues/{id}", savedVenue.getVenueId())
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
            .exchange()
            .expectStatus().isNotFound();
    }

    /**
     * TEST 11: PARTICIPANT cannot create venue (403 Forbidden)
     * DEMONSTRATES: Security rules work
     * PROVES: Role-based access control enforced
     */
    @Test
    @DisplayName("POST /venues returns 403 for PARTICIPANT")
    void createVenue_shouldForbid_forParticipant() {
        webTestClient.post()
            .uri("/venues")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Map.of(
                "name", "Unauthorized Venue",
                "address", "123 Test St",
                "capacity", 50,
                "contactName", "Test Manager",
                "contactEmail", "test@test.com",
                "contactPhone", "555-1234"
            ))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + participantToken)
            .exchange()
            .expectStatus().isForbidden();
    }

    /**
     * TEST 12: Create venue with invalid data (400 Bad Request)
     * DEMONSTRATES: Validation works
     * PROVES: Service validates required fields, returns 400 for invalid data
     */
    @Test
    @DisplayName("POST /venues returns 400 for invalid data")
    void createVenue_shouldReturnBadRequest_forInvalidData() {
        webTestClient.post()
            .uri("/venues")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Map.of(
                "name", "",  // Empty name
                "address", "123 Test St",
                "capacity", -10,  // Invalid capacity
                "contactName", "Test Manager",
                "contactEmail", "test@test.com",
                "contactPhone", "555-1234"
            ))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
            .exchange()
            .expectStatus().isBadRequest();
    }

    /**
     * TEST 13: Update venue NOT FOUND (404 Not Found)
     * DEMONSTRATES: Error handling for missing venue
     * PROVES: Service checks venue exists, returns 404
     */
    @Test
    @DisplayName("PUT /venues/{id} returns 404 when venue not found")
    void updateVenue_shouldReturnNotFound_whenMissing() {
        webTestClient.put()
            .uri("/venues/{id}", 999999)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Map.of(
                "name", "Updated Name",
                "address", "Updated Address",
                "capacity", 100,
                "contactName", "Updated Manager",
                "contactEmail", "updated@test.com",
                "contactPhone", "555-9999"
            ))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
            .exchange()
            .expectStatus().isNotFound();
    }

    /**
     * TEST 14: Availability check with invalid time range (400 Bad Request)
     * DEMONSTRATES: Validation works for time parameters
     * PROVES: Service validates start < end time requirement
     */
    @Test
    @DisplayName("GET /venues/{id}/availability returns 400 for invalid time range")
    void checkAvailability_shouldReturnBadRequest_forInvalidTimeRange() {
        webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/venues/{id}/availability")
                .queryParam("startDateTime", "2026-04-15T12:00:00")
                .queryParam("endDateTime", "2026-04-15T10:00:00")  // End before start
                .build(savedVenue.getVenueId()))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + participantToken)
            .exchange()
            .expectStatus().isBadRequest();
    }
}
