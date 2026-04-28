
# SFWE405-505Project

Event Planning & Coordination Platform  
**Phase 2: Secure Backend with Validation, Business Rules, and Testing**

This phase extends the Phase 1 backend by adding authentication, authorization, validation, business rule enforcement, integration testing, and load testing. The primary focus of this phase is the backend. A frontend has been created, but it is minimal and not the main deliverable for this phase.

---

## Overview

The Event Planning & Coordination Platform is a Spring Boot application backed by PostgreSQL.  
Phase 2 demonstrates:

- JWT-based authentication
- Role-based authorization with Spring Security
- Input validation and structured error handling
- Business rule enforcement in service logic
- Integration testing with real HTTP requests and database usage
- Load testing with Gatling

---

## Tech Stack

- Java 17
- Spring Boot
  - Spring Web
  - Spring Data JPA
  - Spring Security
- PostgreSQL
- Docker Compose
- Maven
- WebTestClient
- JUnit
- Gatling

---

## Backend Project Structure

```text
backend/
├── src
│   ├── main
│   │   ├── java/com/example/demo
│   │   │   ├── config          # application/security/CORS/seeding configuration
│   │   │   ├── controller      # REST API endpoints
│   │   │   ├── entity          # JPA entities and domain models
│   │   │   ├── repository      # Spring Data JPA repositories
│   │   │   ├── security        # JWT utilities and authentication filter
│   │   │   ├── service         # business logic and rule enforcement
│   │   │   └── DemoApplication.java
│   │   └── resources           # application properties and backend resources
│   └── test
│       ├── java/com/example/demo   # integration tests and load simulation
│       └── resources               # test resources
├── pom.xml
└── Dockerfile.backend
```

---

## User Roles

The system supports the following roles:

* `ADMIN`
* `STAFF`
* `PARTICIPANT`
* `ORGANIZER`

Authorization is enforced using Spring Security annotations:

```java
@PreAuthorize("hasAnyRole('ADMIN','ORGANIZER','PARTICIPANT')")
```

Example: registration creation is accessible to `ADMIN`, `ORGANIZER`, and `PARTICIPANT`, while `STAFF` is denied.

---

## Docker Services

* `db` → PostgreSQL main database (5432)
* `db-test` → PostgreSQL test database (5433)
* `backend` → Spring Boot API (8080)
* `frontend` → React app (5173)

---

## Running the Application

```bash
docker compose up -d --build
```

Verify:

```bash
docker ps
```

Stop:

```bash
docker compose down
```

Reset DB:

```bash
docker compose down -v
```

---

## Backend Access

```
http://localhost:8080
```

---

## Authentication

### Login

```http
POST /auth/login
```

```json
{
  "username": "admin",
  "password": "admin123"
}
```

### Use Token

```
Authorization: Bearer <token>
```

---

## Validation & Error Handling

Validation uses:

* `@Valid`
* `@NotBlank`
* `@Email`
* `@NotNull`

Global handling via:

```java
@RestControllerAdvice
```

### Common Responses

* `200 OK`
* `204 No Content`
* `400 Bad Request`
* `403 Forbidden`
* `404 Not Found`
* `409 Conflict`
* `500 Internal Server Error`

---

## Integration Testing

Run all automated tests:

```bash
mvn test
```

Tests use:

* WebTestClient
* real HTTP requests
* test database (`db-test`)

### Coverage

* authentication flow
* secured endpoints
* validation errors
* business rules
* role-based access control

---

## Load Testing

Run Gatling simulation:

```bash
mvn gatling:test -Dgatling.simulationClass=com.example.demo.LoadSimulation
```

### Results

* 100% success rate
* 0 failures
* ~76 ms average response time
* 95% < 150 ms
* max < 500 ms

Assertions:

* response time < 3000 ms
* success rate > 95%

---

## Javadocs

Generate:

```bash
mvn javadoc:javadoc
```

Open:

```
backend/target/site/apidocs/index.html
```

---

## Database Inspection

```bash
docker exec -it event_db psql -U postgres -d eventdb
```

---

## Phase 2 Summary

Phase 2 delivers a secure and robust backend with authentication, validation, business rule enforcement, integration testing, and load testing. The system is fully testable and demonstrates correct behavior under both normal and concurrent usage.

---

## Notes

* Frontend is minimal for this phase
* Backend is the primary deliverable
* Docker is the recommended way to run the system


