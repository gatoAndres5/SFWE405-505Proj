package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.Map;

import com.example.demo.entity.Booking;
import com.example.demo.entity.BookingStatus;
import com.example.demo.entity.Event;
import com.example.demo.entity.EventStatus;
import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import com.example.demo.entity.Vendor;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.VendorRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class BookingIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String organizerToken;
    private String staffToken;

    private Event event;
    private Vendor vendor;
    private Booking booking;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        vendorRepository.deleteAll();
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

        User organizer = new User(
                "organizer",
                "org@test.com",
                passwordEncoder.encode("org123"),
                UserRole.ORGANIZER
        );
        organizer.setEnabled(true);
        userRepository.save(organizer);

        User staff = new User(
                "staff",
                "staff@test.com",
                passwordEncoder.encode("staff123"),
                UserRole.STAFF
        );
        staff.setEnabled(true);
        userRepository.save(staff);

        event = new Event(
                "Test Event",
                "Booking event",
                EventStatus.ACTIVE,
                LocalDateTime.parse("2026-05-10T10:00:00"),
                LocalDateTime.parse("2026-05-10T18:00:00")
        );
        event = eventRepository.save(event);

        vendor = new Vendor();
        vendor.setName("Catering Co");
        vendor.setActive(true);
        vendor = vendorRepository.save(vendor);

        adminToken = loginAndGetToken("admin", "admin123");
        organizerToken = loginAndGetToken("organizer", "org123");
        staffToken = loginAndGetToken("staff", "staff123");

        assertNotNull(adminToken);
        assertNotNull(organizerToken);
        assertNotNull(staffToken);
    }

    @SuppressWarnings("unchecked")
    private String loginAndGetToken(String username, String password) {
        Map<String, Object> response = webTestClient.post()
                .uri("/auth/login")
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
    @DisplayName("POST /bookings creates booking for ADMIN")
    void createBooking_shouldSucceed_forAdmin() {
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/bookings")
                        .queryParam("eventId", event.getId())
                        .queryParam("vendorId", vendor.getId())
                        .queryParam("serviceDescription", "Food service")
                        .queryParam("startDateTime", "2026-05-10T12:00:00")
                        .queryParam("endDateTime", "2026-05-10T14:00:00")
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.serviceDescription").isEqualTo("Food service")
                .jsonPath("$.bookingStatus").isEqualTo("REQUESTED");
    }
    @Test
    @DisplayName("POST /bookings creates booking for ORGANIZER")
    void createBooking_shouldSucceed_forOrganizer() {
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/bookings")
                        .queryParam("eventId", event.getId())
                        .queryParam("vendorId", vendor.getId())
                        .queryParam("serviceDescription", "Organizer booking")
                        .queryParam("startDateTime", "2026-05-10T14:30:00")
                        .queryParam("endDateTime", "2026-05-10T15:30:00")
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + organizerToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.serviceDescription").isEqualTo("Organizer booking")
                .jsonPath("$.bookingStatus").isEqualTo("REQUESTED");
    }

        @Test
        @DisplayName("POST /bookings returns 404 when event not found")
        void createBooking_shouldReturnNotFound_whenEventMissing() {
        long before = bookingRepository.count();

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/bookings")
                        .queryParam("eventId", 999999L)
                        .queryParam("vendorId", vendor.getId())
                        .queryParam("serviceDescription", "Food service")
                        .queryParam("startDateTime", "2026-05-10T12:00:00")
                        .queryParam("endDateTime", "2026-05-10T14:00:00")
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .exchange()
                .expectStatus().isNotFound();

        org.junit.jupiter.api.Assertions.assertEquals(before, bookingRepository.count());
        }

        @Test
        @DisplayName("POST /bookings returns 404 when vendor not found")
        void createBooking_shouldReturnNotFound_whenVendorMissing() {
        long before = bookingRepository.count();

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/bookings")
                        .queryParam("eventId", event.getId())
                        .queryParam("vendorId", 999999L)
                        .queryParam("serviceDescription", "Food service")
                        .queryParam("startDateTime", "2026-05-10T12:00:00")
                        .queryParam("endDateTime", "2026-05-10T14:00:00")
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .exchange()
                .expectStatus().isNotFound();

        org.junit.jupiter.api.Assertions.assertEquals(before, bookingRepository.count());
        }

        @Test
        @DisplayName("POST /bookings returns 409 when event is not ACTIVE")
        void createBooking_shouldReturnConflict_whenEventNotActive() {
        Event draftEvent = eventRepository.save(new Event(
                "Draft Event",
                "Not open for booking",
                EventStatus.DRAFT,
                LocalDateTime.parse("2026-05-11T10:00:00"),
                LocalDateTime.parse("2026-05-11T18:00:00")
        )
        );

        long before = bookingRepository.count();

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/bookings")
                        .queryParam("eventId", draftEvent.getId())
                        .queryParam("vendorId", vendor.getId())
                        .queryParam("serviceDescription", "Food service")
                        .queryParam("startDateTime", "2026-05-11T12:00:00")
                        .queryParam("endDateTime", "2026-05-11T14:00:00")
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .exchange()
                .expectStatus().isEqualTo(409);

        org.junit.jupiter.api.Assertions.assertEquals(before, bookingRepository.count());
        }

        @Test
        @DisplayName("POST /bookings returns 400 for invalid time range")
        void createBooking_shouldReturnBadRequest_whenTimeRangeInvalid() {
        long before = bookingRepository.count();

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/bookings")
                        .queryParam("eventId", event.getId())
                        .queryParam("vendorId", vendor.getId())
                        .queryParam("serviceDescription", "Food service")
                        .queryParam("startDateTime", "2026-05-10T15:00:00")
                        .queryParam("endDateTime", "2026-05-10T13:00:00")
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .exchange()
                .expectStatus().isBadRequest();

        org.junit.jupiter.api.Assertions.assertEquals(before, bookingRepository.count());
        }

        @Test
        @DisplayName("POST /bookings returns 409 for overlapping confirmed booking")
        void createBooking_shouldReturnConflict_whenVendorHasOverlappingConfirmedBooking() {
        Booking existing = new Booking(
                event,
                vendor,
                "Existing confirmed booking",
                LocalDateTime.parse("2026-05-10T12:00:00"),
                LocalDateTime.parse("2026-05-10T14:00:00")
        );
        existing.setBookingStatus(com.example.demo.entity.BookingStatus.CONFIRMED);
        bookingRepository.save(existing);

        long before = bookingRepository.count();

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/bookings")
                        .queryParam("eventId", event.getId())
                        .queryParam("vendorId", vendor.getId())
                        .queryParam("serviceDescription", "Overlapping booking")
                        .queryParam("startDateTime", "2026-05-10T13:00:00")
                        .queryParam("endDateTime", "2026-05-10T15:00:00")
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .exchange()
                .expectStatus().isEqualTo(409);

        org.junit.jupiter.api.Assertions.assertEquals(before, bookingRepository.count());
        }

    @Test
    @DisplayName("POST /bookings returns 403 for STAFF")
    void createBooking_shouldReturnForbidden_forStaff() {
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/bookings")
                        .queryParam("eventId", event.getId())
                        .queryParam("vendorId", vendor.getId())
                        .queryParam("serviceDescription", "Food service")
                        .queryParam("startDateTime", "2026-05-10T12:00:00")
                        .queryParam("endDateTime", "2026-05-10T14:00:00")
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + staffToken)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @DisplayName("GET /bookings returns all bookings")
    void getAllBookings_shouldSucceed() {
        webTestClient.get()
                .uri("/bookings")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("PUT /bookings/{id} updates booking")
    void updateBooking_shouldSucceed() {
        Booking seeded = new Booking(
                event,
                vendor,
                "Initial",
                LocalDateTime.parse("2026-05-10T12:00:00"),
                LocalDateTime.parse("2026-05-10T14:00:00")
        );
        seeded.setBookingStatus(BookingStatus.REQUESTED);
        booking = bookingRepository.save(seeded);

        webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/bookings/" + booking.getBookingId())
                        .queryParam("serviceDescription", "Updated Service")
                        .queryParam("startDateTime", "2026-05-10T13:00:00")
                        .queryParam("endDateTime", "2026-05-10T15:00:00")
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.serviceDescription").isEqualTo("Updated Service");
    }

    @Test
    @DisplayName("PUT /bookings/{id}/confirm works")
    void confirmBooking_shouldSucceed() {
        Booking seeded = new Booking(
                event,
                vendor,
                "Confirm test",
                LocalDateTime.parse("2026-05-10T12:00:00"),
                LocalDateTime.parse("2026-05-10T13:00:00")
        );
        seeded.setBookingStatus(BookingStatus.REQUESTED);
        booking = bookingRepository.save(seeded);

        webTestClient.put()
                .uri("/bookings/" + booking.getBookingId() + "/confirm")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.bookingStatus").isEqualTo("CONFIRMED");
    }

    @Test
    @DisplayName("PUT /bookings/{id}/cancel works")
    void cancelBooking_shouldSucceed() {
        Booking seeded = new Booking(
                event,
                vendor,
                "Cancel test",
                LocalDateTime.parse("2026-05-10T14:30:00"),
                LocalDateTime.parse("2026-05-10T15:30:00")
        );
        seeded.setBookingStatus(BookingStatus.REQUESTED);
        booking = bookingRepository.save(seeded);

        webTestClient.put()
                .uri("/bookings/" + booking.getBookingId() + "/cancel")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.bookingStatus").isEqualTo("CANCELLED");
    }

    @Test
    @DisplayName("PUT /bookings/{id}/complete works")
    void completeBooking_shouldSucceed() {
        Event pastEvent = new Event(
                "Past Event",
                "Already finished",
                EventStatus.ACTIVE,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1)
        );
        pastEvent = eventRepository.save(pastEvent);

        Booking seeded = new Booking(
                pastEvent,
                vendor,
                "Complete test",
                LocalDateTime.now().minusDays(2).plusHours(1),
                LocalDateTime.now().minusDays(2).plusHours(2)
        );
        seeded.setBookingStatus(BookingStatus.CONFIRMED);
        booking = bookingRepository.save(seeded);

        webTestClient.put()
                .uri("/bookings/" + booking.getBookingId() + "/complete")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.bookingStatus").isEqualTo("COMPLETED");
    }
}