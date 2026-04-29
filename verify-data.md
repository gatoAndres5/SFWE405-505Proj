# Database Seeding Verification

## ✅ Success! Database has been populated with sample data.

### What was created:
- **5 Users** with different roles and login credentials
- **5 Participants** (speakers, attendees, staff)
- **4 Venues** (various capacities and locations)
- **4 Vendors** (catering, photography, AV, florist)
- **3 Events** (tech conference, workshop, networking)
- **4 Schedule Items** (keynotes, sessions, breaks)
- **4 Registrations** (confirmed and waitlisted)
- **4 Bookings** (confirmed and requested)
- **3 Event Assignments** (organizer and staff roles)

## Access Information

### Frontend
- **URL**: http://localhost:5173
- React application should be running

### Backend API
- **URL**: http://localhost:8080
- All endpoints are secured and require authentication

### Database
- **Host**: localhost:5432
- **Database**: eventdb
- **User**: postgres
- **Password**: password

## Login Credentials for Testing

| Username | Password | Role | Description |
|----------|----------|------|-------------|
| admin | admin123 | ADMIN | System administrator |
| organizer | password123 | ADMIN | Event organizer |
| staff | password123 | STAFF | Event staff member |
| jsmith | password123 | PARTICIPANT | John Smith (Speaker) |
| jdoe | password123 | PARTICIPANT | Jane Doe (Attendee) |
| mwilson | password123 | PARTICIPANT | Mike Wilson (Staff) |

## Sample Data Highlights

### Events
1. **Tech Innovation Summit 2024** (ACTIVE)
   - 2-day conference starting in 2 weeks
   - Multiple venues and vendors assigned
   - Keynote and technical sessions scheduled

2. **Leadership Excellence Workshop** (ACTIVE)
   - 1-day intensive workshop
   - Interactive team building sessions

3. **Startup Networking Night** (DRAFT)
   - Evening networking event
   - Casual atmosphere for founders and investors

### Venues
- **Grand Ballroom** (500 capacity) - Main event space
- **Conference Room A** (100 capacity) - Smaller meetings
- **Garden Terrace** (200 capacity) - Outdoor events
- **Innovation Lab** (50 capacity) - Tech workshops

### Vendors
- **Gourmet Catering Co** - Food services
- **Capture Moments Photography** - Event photography
- **Sound & Vision AV** - Audio/visual equipment
- **Bloom & Blossom Florists** - Floral arrangements

## Testing the UI

1. Navigate to http://localhost:5173
2. Login with any of the credentials above
3. Browse events, venues, and vendors
4. Test CRUD operations
5. Verify relationships between entities

## Resetting the Database

To completely reset and reseed:
```bash
docker compose down -v
docker compose up -d --build
```

The seeder will automatically run when the database is empty.
