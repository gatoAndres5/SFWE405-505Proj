

#  SFWE405-505Proj

## Event Planning System

**Full-Stack Application with Secure Backend, Business Rules, and Use Case Workflows**

---

##  Overview

The Event Planning System is a full-stack web application designed to manage events, vendors, bookings, participants, and registrations.

The system provides:

* Secure authentication and role-based authorization
* End-to-end business workflows (vendor management, booking creation, etc.)
* Strong backend validation and business rule enforcement
* A React frontend that reflects real-time system behavior

This project demonstrates a complete software system from **API design → business logic → frontend integration → testing**.

---

##  Tech Stack

### Backend

* Java 17
* Spring Boot

  * Spring Web
  * Spring Data JPA
  * Spring Security (JWT)
* PostgreSQL
* Maven

### Frontend

* React (Vite)
* Axios / Fetch API
* CSS

### DevOps / Testing

* Docker Compose
* JUnit + WebTestClient (integration testing)
* Gatling (load testing)

---

##  System Architecture

```text
Frontend (React)
        ↓
Backend (Spring Boot REST API)
        ↓
PostgreSQL Database
```

* Frontend communicates via REST API using JWT authentication
* Backend enforces validation, business rules, and authorization
* Database persists all system entities

---

##  Project Structure

### Backend

```text
backend/
├── controller      # REST endpoints
├── service         # business logic and rules
├── entity          # domain models
├── repository      # database access
├── security        # JWT authentication
├── config          # app/security configuration
└── test            # integration + load tests
```

---

### Frontend

```text
frontend/
├── src
│   ├── pages
│   │   ├── Bookings         # booking workflows
│   │   ├── Dashboard        # role-based dashboard
│   │   ├── Events
│   │   ├── Vendors
│   │   ├── Participants
│   │   ├── Registrations
│   │   ├── ScheduleItems
│   │   ├── Venues
│   │   ├── MyAccount
│   │   ├── LoginPage.jsx
│   │   ├── SignupPage.jsx
│   │   ├── ForgotPasswordPage.jsx
│   │   └── ResetPasswordPage.jsx
│   ├── App.jsx
│   └── main.jsx
├── Dockerfile
└── package.json
```

---

##  User Roles

The system supports:

* `ADMIN`
* `ORGANIZER`
* `STAFF`
* `PARTICIPANT`

Authorization is enforced via:

```java
@PreAuthorize("hasAnyRole('ADMIN','ORGANIZER')")
```
In this system, a distinction exists between a User and a Participant:

- User:
  - Represents an authenticated account
  - Contains credentials, role, and access control information
  - Used for authentication and authorization (JWT)

- Participant:
  - Represents a domain entity associated with event participation
  - Stores participant-specific data (e.g., registrations)
  - Linked to a User account (one-to-one relationship)

Important:
- A User with role PARTICIPANT may have an associated Participant entity
- Some system operations (e.g., viewing registered event bookings) require this linkage
- If a Participant entity is not linked, certain actions may fail

---

##  Authentication

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

### Usage

```
Authorization: Bearer <token>
```

---



##  Integration Testing

Run tests:

```bash
mvn test
```

Covers:

* Authentication
* Authorization
* Validation errors
* Business rules
* API responses

---

##  Load Testing

Run Gatling:

```bash
mvn gatling:test -Dgatling.simulationClass=com.example.demo.LoadSimulation
```

Results:

* 100% success rate
* ~76 ms average response time
* 95% < 150 ms

---

##  Running the Application

```bash
docker compose up -d --build
```

Access:

* Backend → [http://localhost:8080](http://localhost:8080)
* Frontend → [http://localhost:5173](http://localhost:5173)

Stop:

```bash
docker compose down
```

Reset DB:

```bash
docker compose down -v
```

---

##  Environment Configuration (Password Reset)

To enable password reset email functionality, create a `.env` file with:

```env
MAIL_USERNAME=your_email@example.com
MAIL_PASSWORD=your_email_password
MAIL_FROM=your_email@example.com
```

### Notes:

* Do NOT commit `.env` to GitHub
* Use app-specific passwords (e.g., Gmail)
* Ensure mail server configuration matches your provider

---

##  Javadocs

Generate:

```bash
mvn javadoc:javadoc
```

Open:

```
backend/target/site/apidocs/index.html
```

---

##  Database Access

```bash
docker exec -it event_db psql -U postgres -d eventdb
```

---

##  Summary

This project demonstrates a complete full-stack system with:

* Secure authentication and authorization
* Strong validation and business rule enforcement
* Fully implemented user workflows
* Integration and load testing
* React frontend integrated with backend APIs

---

##  Notes

* Docker is the recommended way to run the system
* Backend enforces all critical validation and business rules
* Frontend reflects backend behavior through real-time feedback

---


