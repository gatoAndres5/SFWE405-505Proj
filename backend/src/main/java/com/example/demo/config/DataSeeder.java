package com.example.demo.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.demo.entity.*;
import com.example.demo.repository.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Component
@Order(2) // Run after AdminSeeder
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final VenueRepository venueRepository;
    private final ParticipantRepository participantRepository;
    private final VendorRepository vendorRepository;
    private final EventRepository eventRepository;
    private final ScheduleItemRepository scheduleItemRepository;
    private final RegistrationRepository registrationRepository;
    private final BookingRepository bookingRepository;
    private final EventAssignmentRepository eventAssignmentRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(
            UserRepository userRepository,
            VenueRepository venueRepository,
            ParticipantRepository participantRepository,
            VendorRepository vendorRepository,
            EventRepository eventRepository,
            ScheduleItemRepository scheduleItemRepository,
            RegistrationRepository registrationRepository,
            BookingRepository bookingRepository,
            EventAssignmentRepository eventAssignmentRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.venueRepository = venueRepository;
        this.participantRepository = participantRepository;
        this.vendorRepository = vendorRepository;
        this.eventRepository = eventRepository;
        this.scheduleItemRepository = scheduleItemRepository;
        this.registrationRepository = registrationRepository;
        this.bookingRepository = bookingRepository;
        this.eventAssignmentRepository = eventAssignmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 6) {
            return; // Data already seeded (admin + 5 users = 6)
        }

        // Create admin user if not exists (AdminSeeder might not have run)
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User("admin", "admin@example.com", passwordEncoder.encode("admin123"), UserRole.ADMIN);
            admin.setEnabled(true);
            userRepository.save(admin);
            System.out.println("Default admin user created: admin / admin123");
        }

        // Create sample users
        User organizer = createUser("organizer", "organizer@eventpro.com", UserRole.ADMIN);
        User staff = createUser("staff", "staff@eventpro.com", UserRole.STAFF);
        User participant1 = createUser("jsmith", "jsmith@email.com", UserRole.PARTICIPANT);
        User participant2 = createUser("jdoe", "jdoe@email.com", UserRole.PARTICIPANT);
        User participant3 = createUser("mwilson", "mwilson@email.com", UserRole.PARTICIPANT);

        // Create sample participants
        Participant speaker1 = createParticipant("John", "Smith", "jsmith@email.com", "555-0101", Participant.Role.SPEAKER, participant1);
        Participant attendee1 = createParticipant("Jane", "Doe", "jdoe@email.com", "555-0102", Participant.Role.ATTENDEE, participant2);
        Participant staff1 = createParticipant("Mike", "Wilson", "mwilson@email.com", "555-0103", Participant.Role.STAFF, participant3);
        Participant attendee2 = createParticipant("Sarah", "Johnson", "sarah@email.com", "555-0104", Participant.Role.ATTENDEE, null);
        Participant speaker2 = createParticipant("Dr. Emily", "Brown", "emily@email.com", "555-0105", Participant.Role.SPEAKER, null);

        // Create sample venues
        Venue mainHall = createVenue("Grand Ballroom", "123 Main St", "Metropolis", "NY", "10001", "USA", 500, "John Manager", "john@venue.com", "555-0201");
        Venue conferenceRoom = createVenue("Conference Room A", "456 Oak Ave", "Metropolis", "NY", "10002", "USA", 100, "Lisa Coordinator", "lisa@venue.com", "555-0202");
        Venue outdoorSpace = createVenue("Garden Terrace", "789 Park Blvd", "Metropolis", "NY", "10003", "USA", 200, "Tom Grounds", "tom@venue.com", "555-0203");
        Venue techLab = createVenue("Innovation Lab", "321 Tech St", "Metropolis", "NY", "10004", "USA", 50, "Alex Tech", "alex@venue.com", "555-0204");

        // Create sample vendors
        Vendor catering = createVendor("Gourmet Catering Co", "Maria Garcia", "555-0301", "maria@catering.com", "789 Food St", "Metropolis", "NY", "10005", "USA");
        Vendor photography = createVendor("Capture Moments Photography", "David Chen", "555-0302", "david@photo.com", "456 Camera Ave", "Metropolis", "NY", "10006", "USA");
        Vendor audioVisual = createVendor("Sound & Vision AV", "Robert Miller", "555-0303", "robert@av.com", "123 Sound St", "Metropolis", "NY", "10007", "USA");
        Vendor florist = createVendor("Bloom & Blossom Florists", "Jennifer White", "555-0304", "jennifer@flowers.com", "321 Garden Ln", "Metropolis", "NY", "10008", "USA");

        // Create sample events
        LocalDateTime startDate = LocalDateTime.now().plusWeeks(2);
        Event techConference = createEvent("Tech Innovation Summit 2024", "Annual technology conference featuring the latest innovations in AI, cloud computing, and digital transformation.", startDate, startDate.plusDays(2), EventStatus.ACTIVE);
        Event workshop = createEvent("Leadership Excellence Workshop", "Intensive workshop on developing leadership skills and team management strategies.", startDate.plusWeeks(1), startDate.plusWeeks(1).plusHours(8), EventStatus.ACTIVE);
        Event networkingEvent = createEvent("Startup Networking Night", "Casual networking event for startup founders and investors to connect and collaborate.", startDate.plusWeeks(3), startDate.plusWeeks(3).plusHours(4), EventStatus.DRAFT);

        // Assign venues to events
        techConference.addVenue(mainHall);
        techConference.addVenue(conferenceRoom);
        workshop.addVenue(conferenceRoom);
        networkingEvent.addVenue(outdoorSpace);

        // Assign vendors to events
        techConference.getVendors().addAll(List.of(catering, photography, audioVisual));
        workshop.getVendors().addAll(List.of(catering, audioVisual));
        networkingEvent.getVendors().addAll(List.of(catering, florist));

        // Save events with relationships
        eventRepository.saveAll(List.of(techConference, workshop, networkingEvent));

        // Create schedule items
        ScheduleItem keynote = createScheduleItem(techConference, mainHall, "Opening Keynote: The Future of AI", "Keynote presentation on artificial intelligence trends", startDate, startDate.plusHours(2), "KEYNOTE");
        ScheduleItem session1 = createScheduleItem(techConference, conferenceRoom, "Cloud Architecture Best Practices", "Technical deep dive into cloud design patterns", startDate.plusHours(2).plusMinutes(30), startDate.plusHours(4).plusMinutes(30), "SESSION");
        ScheduleItem lunch = createScheduleItem(techConference, mainHall, "Networking Lunch", "Buffet lunch and networking opportunity", startDate.plusHours(5), startDate.plusHours(6), "BREAK");
        ScheduleItem workshopSession = createScheduleItem(workshop, conferenceRoom, "Team Building Exercises", "Interactive leadership activities", startDate.plusWeeks(1).plusHours(1), startDate.plusWeeks(1).plusHours(3), "WORKSHOP");

        // Create registrations
        Registration reg1 = createRegistration(techConference, speaker1, RegistrationStatus.CONFIRMED, true, "Speaker registration");
        Registration reg2 = createRegistration(techConference, attendee1, RegistrationStatus.CONFIRMED, false, "Early bird registration");
        Registration reg3 = createRegistration(workshop, attendee2, RegistrationStatus.WAITLISTED, false, "Waitlisted for workshop");
        Registration reg4 = createRegistration(networkingEvent, speaker2, RegistrationStatus.CONFIRMED, false, "Speaker attending networking");

        // Create bookings
        Booking cateringBooking1 = createBooking(techConference, catering, "Full conference catering", startDate, startDate.plusDays(2), BookingStatus.CONFIRMED);
        Booking photoBooking = createBooking(techConference, photography, "Event photography coverage", startDate, startDate.plusDays(2), BookingStatus.CONFIRMED);
        Booking avBooking1 = createBooking(techConference, audioVisual, "Audio visual equipment and support", startDate, startDate.plusDays(2), BookingStatus.CONFIRMED);
        Booking cateringBooking2 = createBooking(workshop, catering, "Workshop lunch and refreshments", startDate.plusWeeks(1), startDate.plusWeeks(1).plusHours(8), BookingStatus.REQUESTED);

        // Create event assignments
        EventAssignment orgAssignment = createEventAssignment(techConference, organizer, EventAssignment.EventAssignmentRole.ORGANIZER);
        EventAssignment staffAssignment1 = createEventAssignment(techConference, staff, EventAssignment.EventAssignmentRole.STAFF);
        EventAssignment staffAssignment2 = createEventAssignment(workshop, staff, EventAssignment.EventAssignmentRole.STAFF);

        System.out.println("Database seeded with sample data!");
        System.out.println("- 5 Users");
        System.out.println("- 5 Participants");
        System.out.println("- 4 Venues");
        System.out.println("- 4 Vendors");
        System.out.println("- 3 Events");
        System.out.println("- 4 Schedule Items");
        System.out.println("- 4 Registrations");
        System.out.println("- 4 Bookings");
        System.out.println("- 3 Event Assignments");
    }

    private User createUser(String username, String email, UserRole role) {
        User user = new User(username, email, passwordEncoder.encode("password123"), role);
        user.setEnabled(true);
        return userRepository.save(user);
    }

    private Participant createParticipant(String firstName, String lastName, String email, String phone, Participant.Role role, User user) {
        Participant participant = Participant.createParticipant(firstName, lastName, email, phone, role);
        if (user != null) {
            participant = participantRepository.save(participant);
            user.setParticipant(participant);
            userRepository.save(user);
        } else {
            participant = participantRepository.save(participant);
        }
        return participant;
    }

    private Venue createVenue(String name, String street, String city, String state, String zip, String country, int capacity, String contactName, String contactEmail, String contactPhone) {
        Address address = new Address(street, city, state, zip, country);
        Venue venue = new Venue(name, address, capacity, contactName, contactEmail, contactPhone);
        return venueRepository.save(venue);
    }

    private Vendor createVendor(String name, String contactName, String phone, String email, String street, String city, String state, String zip, String country) {
        Address address = new Address(street, city, state, zip, country);
        Vendor vendor = new Vendor(name, contactName, phone, email, address, true);
        vendor.setAvailability("Weekdays and weekends, with advance notice");
        return vendorRepository.save(vendor);
    }

    private Event createEvent(String name, String description, LocalDateTime startDateTime, LocalDateTime endDateTime, EventStatus status) {
        Event event = new Event(name, description, startDateTime, endDateTime);
        event.setStatus(status);
        return eventRepository.save(event);
    }

    private ScheduleItem createScheduleItem(Event event, Venue venue, String title, String description, LocalDateTime startDateTime, LocalDateTime endDateTime, String type) {
        ScheduleItem item = new ScheduleItem(event, venue, title, description, startDateTime, endDateTime, type);
        return scheduleItemRepository.save(item);
    }

    private Registration createRegistration(Event event, Participant participant, RegistrationStatus status, boolean checkedIn, String notes) {
        Registration registration = new Registration(event, participant, new Date(), status, checkedIn, notes);
        return registrationRepository.save(registration);
    }

    private Booking createBooking(Event event, Vendor vendor, String serviceDescription, LocalDateTime startDateTime, LocalDateTime endDateTime, BookingStatus status) {
        Booking booking = new Booking(event, vendor, serviceDescription, startDateTime, endDateTime);
        booking.setBookingStatus(status);
        return bookingRepository.save(booking);
    }

    private EventAssignment createEventAssignment(Event event, User user, EventAssignment.EventAssignmentRole role) {
        EventAssignment assignment = new EventAssignment(event, user, role);
        return eventAssignmentRepository.save(assignment);
    }
}
