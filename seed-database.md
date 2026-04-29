# Database Seeding Instructions

## Overview
The `DataSeeder.java` class will automatically populate your database with comprehensive sample data for testing the UI.

## Sample Data Created

### Users (Login Credentials)
- **admin / admin123** - Admin user (created by AdminSeeder)
- **organizer / password123** - Event organizer
- **staff / password123** - Event staff member
- **jsmith / password123** - Participant (John Smith - Speaker)
- **jdoe / password123** - Participant (Jane Doe - Attendee)
- **mwilson / password123** - Participant (Mike Wilson - Staff)

### Venues
1. **Grand Ballroom** - 500 capacity, main event space
2. **Conference Room A** - 100 capacity, smaller meetings
3. **Garden Terrace** - 200 capacity, outdoor events
4. **Innovation Lab** - 50 capacity, tech workshops

### Vendors
1. **Gourmet Catering Co** - Food and beverage services
2. **Capture Moments Photography** - Event photography
3. **Sound & Vision AV** - Audio/visual equipment
4. **Bloom & Blossom Florists** - Floral arrangements

### Events
1. **Tech Innovation Summit 2024** (Published)
   - 2-day conference
   - Multiple venues assigned
   - Keynote and technical sessions
   
2. **Leadership Excellence Workshop** (Published)
   - 1-day intensive workshop
   - Interactive sessions
   
3. **Startup Networking Night** (Draft)
   - Evening networking event
   - Casual atmosphere

### Sample Data Relationships
- Events have multiple venues assigned
- Events have vendors booked for services
- Participants are registered for events
- Schedule items created for event sessions
- Users assigned event roles (organizer/staff)
- Registration statuses include confirmed and waitlist
- Booking statuses include confirmed and pending

## How to Run

### Option 1: Start the Application Normally
```bash
cd backend
./mvnw spring-boot:run
```
The seeder will run automatically when the application starts if the database is empty.

### Option 2: Reset and Reseed
To completely reset the database and reseed:
```bash
cd backend
./mvnw clean spring-boot:run
```

## Verification
Once running, you can:
1. Login with any of the user accounts above
2. Browse events, venues, and vendors in the UI
3. Test CRUD operations on all entities
4. View relationships between entities

## Notes
- The seeder only runs when the database is empty (except for the admin user)
- All timestamps are realistic (events scheduled in the future)
- Passwords are hashed using the same encoder as the application
- Data relationships are properly established for testing joins and queries
