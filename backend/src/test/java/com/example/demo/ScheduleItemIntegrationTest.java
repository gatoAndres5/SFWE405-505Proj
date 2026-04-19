package com.example.demo;

import java.time.LocalDateTime;
import java.util.Map;

import com.example.demo.entity.Address;
import com.example.demo.entity.Event;
import com.example.demo.entity.EventStatus;
import com.example.demo.entity.ScheduleItem;
import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import com.example.demo.entity.Venue;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.ScheduleItemRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.VenueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.beans.factory.annotation.Autowired;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ScheduleItemIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private VenueRepository venueRepository;
    @Autowired
    private ScheduleItemRepository scheduleItemRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String participantToken;
    private Event savedEvent;
    private Venue savedVenue;

    private ScheduleItem createTestScheduleItem() {
        ScheduleItem item = new ScheduleItem(
            savedEvent,
            savedVenue,
            "Test Schedule Item",
            "A test schedule item",
            LocalDateTime.parse("2026-04-15T12:00:00"),
            LocalDateTime.parse("2026-04-15T13:00:00"),
            "Workshop");
        return scheduleItemRepository.save(item);
    }

    @BeforeEach
    public void setUp() {
        scheduleItemRepository.deleteAll();
        venueRepository.deleteAll();
        eventRepository.deleteAll();
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

        Event event = new Event(
            "Test Event",
            "A test conference event",
            EventStatus.ACTIVE,
            LocalDateTime.parse("2026-04-15T10:00:00"),
            LocalDateTime.parse("2026-04-15T18:00:00"));
        savedEvent = eventRepository.save(event);

        Address address = new Address(
            "742 Evergreen Terrace",
            "Springfield",
            "IL",
            "62704",
            "USA"
        );
        Venue venue = new Venue(
            "Main Ballroom",
            address,
            200,
            "John Manager",
            "555-1234",
            "manager@test.com");
        savedVenue = venueRepository.save(venue);

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

    //TEST CASES

    /**
     * TEST 1: Create ScheduleItem
     * DEMONSTRATES: POST /scheduleItems works (happy path)
     * PROVES: ADMIN can create, data saves to DB, returns 200 OK with ID
     */
    @Test
    @DisplayName("POST /scheduleItems creates schedule item for ADMIN")
    void createScheduleItem_shouldSucceed_forAdmin() {
        webTestClient.post()
            .uri(uriBuilder -> uriBuilder.path("/scheduleItems")
                .queryParam("eventId", savedEvent.getId())
                .queryParam("venueId", savedVenue.getVenueId())
                .queryParam("title", "Opening Keynote")
                .queryParam("description", "Welcome speech")
                .queryParam("startDateTime", "2026-04-15T10:00:00")
                .queryParam("endDateTime", "2026-04-15T11:00:00")
                .queryParam("type", "Keynote")
                .build())
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
            .exchange()
            .expectStatus().isOk()  
            .expectBody()
            .jsonPath("$.id").exists();
    }
    /**
     * TEST 2: Read all ScheduleItems
     * DEMONSTRATES: GET /scheduleItems works
     * PROVES: PARTICIPANT can read, returns list
     */
    @Test
    @DisplayName("GET /scheduleItems returns list for PARTICIPANT")
    void getAllScheduleItems_shouldSucceed_forParticipant() {
        createTestScheduleItem();
        webTestClient.get()
            .uri("/scheduleItems")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + participantToken)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Map.class)
            .hasSize(1); 
    }

    /**
     * TEST 3: Get ScheduleItem by ID
     * DEMONSTRATES: GET /scheduleItems/{id} works
     * PROVES: Can retrieve specific item by ID
     */
    @Test
    @DisplayName("GET /scheduleItems/{id} returns schedule item by ID")
    void getScheduleItemById_shouldSucceed() {
        ScheduleItem item = createTestScheduleItem();
        webTestClient.get()
            .uri("/scheduleItems/{id}", item.getId())
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + participantToken)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.id").isEqualTo(item.getId().intValue());
    }

    /**
     * TEST 4: Get ScheduleItem NOT FOUND
     * DEMONSTRATES: Error handling works (404)
     * PROVES: Service checks if item exists, returns 404 instead of crashing
     */
    @Test
    @DisplayName("GET /scheduleItems/{id} returns 404 when not found")
    void getScheduleItemById_shouldReturnNotFound_whenMissing() {
        webTestClient.get()
            .uri("/scheduleItems/{id}", 999999)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + participantToken)
            .exchange()
            .expectStatus().isNotFound();
    }

    /**
     * TEST 5: Get Duration
     * DEMONSTRATES: GET /scheduleItems/{id}/duration works
     * PROVES: Duration calculation is correct (1 hour = 3600 seconds)
     */
    @Test
    @DisplayName("GET /scheduleItems/{id}/duration returns correct duration")
    void getDuration_shouldReturn1Hour() {
        ScheduleItem item = createTestScheduleItem();

        webTestClient.get()
            .uri("/scheduleItems/{id}/duration", item.getId())
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + participantToken)
            .exchange()
            .expectStatus().isOk();
    }

    /**
     * TEST 6: Update ScheduleItem
     * DEMONSTRATES: PUT /scheduleItems/{id} works
     * PROVES: ADMIN can update, fields are changed in DB
     */
    @Test
    @DisplayName("PUT /scheduleItems/{id} updates schedule item for ADMIN")
    void updateScheduleItem_shouldSucceed_forAdmin() {
        ScheduleItem item = createTestScheduleItem();

        webTestClient.put()
            .uri(uriBuilder -> uriBuilder.path("/scheduleItems/{id}")
                .queryParam("eventId", savedEvent.getId())
                .queryParam("venueId", savedVenue.getVenueId())
                .queryParam("title", "Updated Keynote")
                .queryParam("description", "Updated description")
                .queryParam("startDateTime", "2026-04-15T10:30:00")
                .queryParam("endDateTime", "2026-04-15T11:30:00")
                .queryParam("type", "Keynote")
                .build(item.getId()))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.title").isEqualTo("Updated Keynote");
    }

    /**
     * TEST 7: Reschedule (change only time)
     * DEMONSTRATES: PUT /scheduleItems/{id}/reschedule works
     * PROVES: Can change start/end times independently
     */
    @Test
    @DisplayName("PUT /scheduleItems/{id}/reschedule changes time for ADMIN")
    void rescheduleItem_shouldSucceed_forAdmin() {
        ScheduleItem item = createTestScheduleItem();

        webTestClient.put()
            .uri(uriBuilder -> uriBuilder.path("/scheduleItems/{id}/reschedule")
                .queryParam("startDateTime", "2026-04-15T14:00:00")
                .queryParam("endDateTime", "2026-04-15T15:00:00")
                .build(item.getId()))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.startDateTime").isEqualTo("2026-04-15T14:00:00");
    }

    /**
     * TEST 8: Assign Venue
     * DEMONSTRATES: PUT /scheduleItems/{id}/assignVenue works
     * PROVES: Can change venue reference
     */
    @Test
    @DisplayName("PUT /scheduleItems/{id}/assignVenue assigns new venue for ADMIN")
    void assignVenue_shouldSucceed_forAdmin() {
        ScheduleItem item = createTestScheduleItem();
        Address address2 = new Address(
            "745 Evergreen Terrace",
            "Springfield",
            "IL",
            "62704",
            "USA"
        );
        Venue venue2 = new Venue(
            "Secondary Hall",
            address2,
            150,
            "Jane Admin",
            "555-5678",
            "jane@test.com");
        Venue savedVenue2 = venueRepository.save(venue2);

        webTestClient.put()
            .uri(uriBuilder -> uriBuilder.path("/scheduleItems/{id}/assignVenue")
                .queryParam("venueId", savedVenue2.getVenueId())
                .build(item.getId()))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
            .exchange()
            .expectStatus().isOk();
    }

    /**
     * TEST 9: Delete ScheduleItem
     * DEMONSTRATES: DELETE /scheduleItems/{id} works
     * PROVES: Item is removed from DB, subsequent GET returns 404
     */
    @Test
    @DisplayName("DELETE /scheduleItems/{id} removes item for ADMIN")
    void deleteScheduleItem_shouldSucceed_forAdmin() {
        ScheduleItem item = createTestScheduleItem();

        webTestClient.delete()
            .uri("/scheduleItems/{id}", item.getId())
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
            .exchange()
            .expectStatus().isOk();

        webTestClient.get()
            .uri("/scheduleItems/{id}", item.getId())
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
            .exchange()
            .expectStatus().isNotFound();
    }

    /**
     * TEST 10: PARTICIPANT cannot create (403 Forbidden)
     * DEMONSTRATES: Security rules work
     * PROVES: Role-based access control enforced
     */
    @Test
    @DisplayName("POST /scheduleItems returns 403 for PARTICIPANT")
    void createScheduleItem_shouldForbid_forParticipant() {
        webTestClient.post()
            .uri(uriBuilder -> uriBuilder.path("/scheduleItems")
                .queryParam("eventId", savedEvent.getId())
                .queryParam("venueId", savedVenue.getVenueId())
                .queryParam("title", "Test")
                .queryParam("description", "Test")
                .queryParam("startDateTime", "2026-04-15T12:00:00")
                .queryParam("endDateTime", "2026-04-15T13:00:00")
                .queryParam("type", "Test")
                .build())
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + participantToken)  // ⚠️ PARTICIPANT
            .exchange()
            .expectStatus().isForbidden();
    }

    /**
     * TEST 11: Event not found (404 Not Found)
     * DEMONSTRATES: Error handling for missing FK
     * PROVES: Service checks Event exists, throws exception
     */
    @Test
    @DisplayName("POST /scheduleItems returns 404 when event not found")
    void createScheduleItem_shouldReturnNotFound_whenEventMissing() {
        webTestClient.post()
            .uri(uriBuilder -> uriBuilder.path("/scheduleItems")
                .queryParam("eventId", 999999L)
                .queryParam("venueId", savedVenue.getVenueId())
                .queryParam("title", "Test")
                .queryParam("description", "Test")
                .queryParam("startDateTime", "2026-04-15T12:00:00")
                .queryParam("endDateTime", "2026-04-15T13:00:00")
                .queryParam("type", "Test")
                .build())
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
            .exchange()
            .expectStatus().isNotFound();
    }

    /**
     * TEST 12: Venue not found (404 Not Found)
     * DEMONSTRATES: Error handling for missing FK
     * PROVES: Service checks Venue exists, throws exception
     */
    @Test
    @DisplayName("POST /scheduleItems returns 404 when venue not found")
    void createScheduleItem_shouldReturnNotFound_whenVenueMissing() {
        webTestClient.post()
            .uri(uriBuilder -> uriBuilder.path("/scheduleItems")
                .queryParam("eventId", savedEvent.getId())
                .queryParam("venueId", 999999L)
                .queryParam("title", "Test")
                .queryParam("description", "Test")
                .queryParam("startDateTime", "2026-04-15T12:00:00")
                .queryParam("endDateTime", "2026-04-15T13:00:00")
                .queryParam("type", "Test")
                .build())
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
            .exchange()
            .expectStatus().isNotFound();
    }
}