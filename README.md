# SFWE405-505Proj

Event Planning & Coordination Platform (Phase 1): Spring Boot + Spring Data JPA + PostgreSQL (Docker)  
This phase demonstrates a working data model (entities + relationships), repositories, and basic REST endpoints testable via Postman.

---

## Tech Stack
- Java 17
- Spring Boot (Web + Data JPA)
- PostgreSQL (Docker Compose)
- Maven
- Postman (for REST testing)

---

## Prerequisites
Make sure you have:
- Java 17 (`java -version`)
- Maven (`mvn -v`)
- Docker + Docker Compose (`docker --version`, `docker compose version`)

---

## 1) Start PostgreSQL with Docker Compose

From the project root (where `docker-compose.yml` is located):

````bash
docker compose up -d
````

Verify it’s running:

````bash
docker ps
````

You should see a container for Postgres (often named something like `sfwe405-505proj-event_db-1` or `event_db` depending on your compose file).

To stop containers:

```bash
docker compose down
```

To stop containers **and delete the database data (RESET)**:

````bash
docker compose down -v
````

 `-v` deletes the Postgres volume, meaning all tables/data will be recreated fresh next run.

---

## 2) Run the Spring Boot App

From the project root:

```bash
mvn spring-boot:run
```

App should start on:

* API base URL: `http://localhost:8080`

If you run into DB connection issues, confirm Postgres is running (`docker ps`) and your `application.properties` matches your compose config.

---

## 3) Check the Database via Docker (No GUI Needed)

### 3.1 Find the running container name

```bash
docker ps
```

Look for the Postgres container and copy its name (example: `event_db` or `sfwe405-505proj-event_db-1`).

### 3.2 Open a psql shell inside the container

Replace `<POSTGRES_CONTAINER>` with your container name:

```bash
docker exec -it <POSTGRES_CONTAINER> psql -U postgres -d eventdb
```

You should see:

```
eventdb=#
```

### 3.3 Useful psql commands

List tables:

```sql
\dt
```

Describe a table:

```sql
\d participant
\d event
\d registration
\d schedule_item
```

See data:

```sql
SELECT * FROM participant;
SELECT * FROM event;
SELECT * FROM registration;
```

Quit psql:

```sql
\q
```

---

## 4) Postman: Test REST Endpoints



### Postman Collection Link / Location


  * `https://app.getpostman.com/join-team?invite_code=8ec6bfcf9a724ce8c6af9071ce93e4d7b5986d09701db458f3207a687d773e64&target_code=8f41ffcb54974a5160e47a6e0eed114b`

---

## 5) Current Endpoints (Phase 1)

> These endpoints are intentionally simple to demonstrate ORM + repositories.

### Participants

Base: `/participants`

* **Create Participant**

  * `POST http://localhost:8080/participants`
  * Body (example):

    ```json
    {
      "firstName": "Andres",
      "lastName": "Galvez",
      "email": "andres@example.com",
      "phone": "520-555-1234",
      "role": "ATTENDEE",
      "active": true
    }
    ```

* **Get All Participants**

  * `GET http://localhost:8080/participants`

* **Update Participant**

  * `PUT http://localhost:8080/participants/{id}`
  * Body: same shape as create

* **Delete Participant**

  * `DELETE http://localhost:8080/participants/{id}`

---

### Events

Base: `/events` (if you added an EventController)

* **Create Event**

  * `POST http://localhost:8080/events`
  * Body (example):

    ```json
    {
      "name": "Tech Meetup",
      "description": "Networking + talks",
      "startDateTime": "2026-03-01T18:00:00",
      "endDateTime": "2026-03-01T20:00:00",
      "status": "ACTIVE"
    }
    ```

* **Get All Events**

  * `GET http://localhost:8080/events`

---

### Registrations

Base: `/registrations`

* **Create Registration**

  * `POST http://localhost:8080/registrations?eventId={eventId}&participantId={participantId}`

* **Get All Registrations**

  * `GET http://localhost:8080/registrations`

---

## 6) Common Notes / Troubleshooting

### Schema not updating?

If you changed entity mappings and don’t see changes in Postgres:

* easiest clean reset:

```bash
docker compose down -v
docker compose up -d
mvn spring-boot:run
```

### Recursion in JSON response (nested objects)

If you see deep nested recursion when returning entities with relationships, that’s normal with bidirectional JPA mappings.
For Phase 1, it’s ok. Later we can fix cleanly using:

* DTOs
* `@JsonIgnore` / `@JsonManagedReference` / `@JsonBackReference`

---

## 7) Team Workflow Notes

* Everyone running this locally will have their **own local Postgres** container + volume.
* If you want a fresh DB state: `docker compose down -v` then bring it back up.

---




