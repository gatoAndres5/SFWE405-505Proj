package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.Map;

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

import com.example.demo.entity.Address;
import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import com.example.demo.entity.Vendor;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.VendorRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class VendorIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String staffToken;
    private Vendor sarahVendor;

    @BeforeEach
    void setUp() {
        vendorRepository.deleteAll();
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

        Address address = new Address(
                "Baghdad Street",
                "Baghdad",
                "BG",
                "10001",
                "Iraq"
        );

        sarahVendor = new Vendor(
                "Sarah Events",
                "Sarah",
                "1234567890",
                "sarah@test.com",
                address,
                true
        );
        sarahVendor = vendorRepository.save(sarahVendor);

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
    @DisplayName("POST /vendors creates vendor with active=true and vendorId")
    void createVendorTest() {
        webTestClient.post()
                .uri("/vendors")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "name": "Sarah Events 2",
                          "contactName": "Sarah",
                          "contactPhone": "1234567890",
                          "contactEmail": "sarah2@test.com",
                          "address": {
                                "street": "Baghdad Street",
                                "city": "Baghdad",
                                "state": "BG",
                                "zipCode": "10001",
                                "country": "Iraq"
                        }
                        }
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.name").isEqualTo("Sarah Events 2")
                .jsonPath("$.contactEmail").isEqualTo("sarah2@test.com")
                .jsonPath("$.active").isEqualTo(true);
    }

    @Test
    @DisplayName("POST /vendors returns 400 when required fields are missing")
    void createVendor_shouldReturnBadRequest_whenRequiredFieldsMissing() {
        webTestClient.post()
                .uri("/vendors")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "contactName": "Sarah",
                          "contactPhone": "1234567890",
                          "contactEmail": "missingname@test.com"
                        }
                        """)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("POST /vendors returns 400 when contact email format is invalid")
    void createVendor_shouldReturnBadRequest_whenEmailInvalid() {
        webTestClient.post()
                .uri("/vendors")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "name": "Invalid Email Vendor",
                          "contactName": "Sarah",
                          "contactPhone": "1234567890",
                          "contactEmail": "not-an-email",
                          "address": {
                                "street": "Baghdad Street",
                                "city": "Baghdad",
                                "state": "BG",
                                "zipCode": "10001",
                                "country": "Iraq"
                        }
                        }
                        """)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("POST /vendors returns 409 when contact email already exists")
    void createVendor_shouldReturnConflict_whenDuplicateEmail() {
        webTestClient.post()
                .uri("/vendors")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "name": "First Vendor",
                          "contactName": "Sarah",
                          "contactPhone": "1234567890",
                          "contactEmail": "duplicate@test.com",
                          "address": {
                                "street": "Baghdad Street",
                                "city": "Baghdad",
                                "state": "BG",
                                "zipCode": "10001",
                                "country": "Iraq"
                        }
                        }
                        """)
                .exchange()
                .expectStatus().isOk();

        webTestClient.post()
                .uri("/vendors")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "name": "Second Vendor",
                          "contactName": "Jane",
                          "contactPhone": "9998887777",
                          "contactEmail": "duplicate@test.com",
                          "address": {
                                "street": "Another Street",
                                "city": "Baghdad",
                                "state": "BG",
                                "zipCode": "10001",
                                "country": "Iraq"
                        }
                        }
                        """)
                .exchange()
                .expectStatus().isEqualTo(409);
    }

    @Test
    @DisplayName("POST /vendors returns 403 for STAFF token")
    void createVendor_shouldReturnForbidden_forStaff() {
        webTestClient.post()
                .uri("/vendors")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + staffToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "name": "Staff Should Fail",
                          "contactName": "Sarah",
                          "contactPhone": "1234567890",
                          "contactEmail": "stafffail@test.com",
                          "address": {
                                "street": "Baghdad Street",
                                "city": "Baghdad",
                                "state": "BG",
                                "zipCode": "10001",
                                "country": "Iraq"
                        }
                        }
                        """)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @DisplayName("GET /vendors returns all vendors for ADMIN")
    void getAllVendorsTest() {
        webTestClient.get()
                .uri("/vendors")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].name").exists();
    }

    @Test
    @DisplayName("GET /vendors/{id} returns vendor by id")
    void getVendorByIdTest() {
        webTestClient.get()
                .uri("/vendors/" + sarahVendor.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Sarah Events");
    }

    @Test
    @DisplayName("PUT /vendors/{id} updates vendor")
    void updateVendorTest() {
        webTestClient.put()
                .uri("/vendors/" + sarahVendor.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "name": "Sarah Updated",
                          "contactName": "Sarah",
                          "contactPhone": "2223334444",
                          "contactEmail": "updated@sarah.com",
                          "address": {
                                "street": "Baghdad Second Street",
                                "city": "Baghdad",
                                "state": "BG",
                                "zipCode": "10001",
                                "country": "Iraq"
                                },
                          "active": true
                        }
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Sarah Updated");
    }

    @Test
    @DisplayName("PATCH /vendors/{id}/deactivate deactivates vendor")
    void deactivateVendorTest() {
        webTestClient.patch()
                .uri("/vendors/" + sarahVendor.getId() + "/deactivate")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.active").isEqualTo(false);
    }

    @Test
    @DisplayName("DELETE /vendors/{id} deletes vendor")
    void deleteVendorTest() {
        webTestClient.delete()
                .uri("/vendors/" + sarahVendor.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .exchange()
                .expectStatus().isOk();
    }
}