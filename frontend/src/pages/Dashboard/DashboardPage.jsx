import { useEffect, useMemo, useState } from "react";
import axios from "axios";
import RoleDashboard from "./RoleDashboard";
import "./Dashboard.css";

const API_BASE = "http://localhost:8080";

export default function DashboardPage() {
  const [dashboardData, setDashboardData] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const token = localStorage.getItem("token");

  function parseJwt(tokenValue) {
    try {
      if (!tokenValue) return null;
      const payload = tokenValue.split(".")[1];
      return JSON.parse(atob(payload));
    } catch {
      return null;
    }
  }

  const jwtPayload = useMemo(() => parseJwt(token), [token]);

  const realRole =
    jwtPayload?.role ||
    jwtPayload?.authorities ||
    jwtPayload?.userRole ||
    null;

  const username =
    jwtPayload?.sub ||
    jwtPayload?.username ||
    jwtPayload?.userName ||
    "User";

  const userId =
    jwtPayload?.id ||
    jwtPayload?.userId ||
    jwtPayload?.user_id ||
    null;

  const user = {
    name: username,
    role: realRole,
  };

  useEffect(() => {
    async function fetchAdminDashboardData() {
      try {
        const config = {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        };

        const [eventsRes, participantsRes, bookingsRes, registrationsRes] =
          await Promise.all([
            axios.get(`${API_BASE}/events`, config),
            axios.get(`${API_BASE}/participants`, config),
            axios.get(`${API_BASE}/bookings`, config),
            axios.get(`${API_BASE}/registrations`, config),
          ]);

        const events = eventsRes.data ?? [];
        const participants = participantsRes.data ?? [];
        const bookings = bookingsRes.data ?? [];
        const registrations = registrationsRes.data ?? [];

        const now = new Date();

        const activeEvents = events.filter((event) => {
          if (!event.startDateTime || !event.endDateTime) return false;
          const start = new Date(event.startDateTime);
          const end = new Date(event.endDateTime);
          return start <= now && end >= now;
        });

        const pendingRegistrations = registrations.filter(
          (registration) => registration.registrationStatus === "PENDING"
        );

        const activeBookings = bookings.filter(
          (booking) =>
            booking.bookingStatus === "REQUESTED" ||
            booking.bookingStatus === "CONFIRMED"
        );

        const upcomingEvents = events
          .filter((event) => event.startDateTime)
          .filter((event) => new Date(event.startDateTime) > now)
          .sort(
            (a, b) => new Date(a.startDateTime) - new Date(b.startDateTime)
          )
          .slice(0, 5);

        setDashboardData({
          events,
          participants,
          bookings,
          registrations,
          stats: {
            totalEvents: events.length,
            activeEvents: activeEvents.length,
            totalParticipants: participants.length,
            pendingRegistrations: pendingRegistrations.length,
            activeBookings: activeBookings.length,
          },
          upcomingEvents,
        });
      } catch (err) {
        console.error(err);
        setError("Failed to load admin dashboard data.");
      } finally {
        setLoading(false);
      }
    }

    async function fetchStaffDashboardData() {
  try {
    const config = {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    };


    const [eventsRes, bookingsRes, registrationsRes, scheduleItemsRes] =
      await Promise.allSettled([
        axios.get(`${API_BASE}/users/me/events`, config),
        axios.get(`${API_BASE}/bookings`, config),
        axios.get(`${API_BASE}/registrations`, config),
        axios.get(`${API_BASE}/scheduleItems/my`, config),
      ]);

    const events =
      eventsRes.status === "fulfilled" ? eventsRes.value.data ?? [] : [];
    const bookings =
      bookingsRes.status === "fulfilled" ? bookingsRes.value.data ?? [] : [];
    const registrations =
      registrationsRes.status === "fulfilled"
        ? registrationsRes.value.data ?? []
        : [];
    const scheduleItems =
      scheduleItemsRes.status === "fulfilled"
        ? scheduleItemsRes.value.data ?? []
        : [];

    const now = new Date();

    const pendingRegistrations = registrations.filter(
      (registration) => registration.registrationStatus === "PENDING"
    );

    const activeBookings = bookings.filter(
      (booking) =>
        booking.bookingStatus === "REQUESTED" ||
        booking.bookingStatus === "CONFIRMED"
    );

    const assignedEvents = events
      .filter((event) => event.startDateTime)
      .filter((event) => new Date(event.startDateTime) > now)
      .sort(
        (a, b) => new Date(a.startDateTime) - new Date(b.startDateTime)
      )
      .slice(0, 5)
      .map((event) => {
        const eventRegistrations = registrations.filter((registration) => {
          const registrationEventId =
            registration.event?.id ?? registration.eventId;
          return registrationEventId === event.id;
        });

        return {
          ...event,
          registrationCount: eventRegistrations.length,
        };
      });

    setDashboardData({
      events,
      bookings,
      registrations,
      scheduleItems,
      stats: {
        assignedEvents: events.length,
        pendingRegistrations: pendingRegistrations.length,
        activeBookings: activeBookings.length,
        scheduleItems: scheduleItems.length,
      },
      assignedEvents,
    });
  } catch (err) {
    console.error("staff dashboard fetch failed:", err);
    setError("Failed to load staff dashboard data.");
  } finally {
    setLoading(false);
  }
}

    async function fetchParticipantDashboardData() {
      try {
        const config = {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        };

        const [registrationsRes, eventsRes] = await Promise.all([
          axios.get(`${API_BASE}/registrations`, config),
          axios.get(`${API_BASE}/events`, config),
        ]);

        const registrations = registrationsRes.data ?? [];
        const events = eventsRes.data ?? [];
        const now = new Date();

        const approvedRegistrations = registrations.filter(
          (registration) => registration.registrationStatus === "CONFIRMED"
        );

        const pendingRegistrations = registrations.filter(
          (registration) => registration.registrationStatus === "PENDING"
        );

        const upcomingEvents = registrations
          .map((registration) => {
            const registrationEventId =
              registration.event?.id ?? registration.eventId;

            const eventFromRegistration = registration.event ?? null;

            const matchedEvent =
              eventFromRegistration ||
              events.find((event) => event.id === registrationEventId);

            if (!matchedEvent || !matchedEvent.startDateTime) {
              return null;
            }

            return {
              id: matchedEvent.id,
              name: matchedEvent.name,
              startDateTime: matchedEvent.startDateTime,
              endDateTime: matchedEvent.endDateTime,
              venueName:
                matchedEvent.venue?.name ||
                matchedEvent.venueName ||
                (matchedEvent.venues && matchedEvent.venues.length > 0
                  ? matchedEvent.venues[0].name
                  : null),
              registrationStatus: registration.registrationStatus,
            };
          })
          .filter(Boolean)
          .filter((event) => new Date(event.startDateTime) > now)
          .filter((event) => event.registrationStatus !== "CANCELLED")
          .sort((a, b) => new Date(a.startDateTime) - new Date(b.startDateTime));

        setDashboardData({
          registrations,
          events,
          stats: {
            myRegistrations: registrations.length,
            approvedRegistrations: approvedRegistrations.length,
            pendingRegistrations: pendingRegistrations.length,
            upcomingEvents: upcomingEvents.length,
          },
          upcomingEvents,
        });
      } catch (err) {
        console.error(err);

        if (err.response?.status === 400) {
          setError(
            "Please complete your participant profile on the Participants page before viewing your dashboard."
          );
        } else if (err.response?.status === 403) {
          setError("You are not authorized to view participant dashboard data.");
        } else {
          setError("Failed to load participant dashboard data.");
        }
      } finally {
        setLoading(false);
      }
    }

    if (!token) {
      setError("No authentication token found.");
      setLoading(false);
      return;
    }

    if (!realRole) {
      setError("Could not determine user role.");
      setLoading(false);
      return;
    }

    if (realRole === "ADMIN") {
      fetchAdminDashboardData();
    } else if (realRole === "STAFF" || realRole === "ORGANIZER") {
      fetchStaffDashboardData();
    } else if (realRole === "PARTICIPANT") {
      fetchParticipantDashboardData();
    } else {
      setError("No dashboard available for this role.");
      setLoading(false);
    }
  }, [token, realRole]);

  if (loading) {
    return <p>Loading dashboard...</p>;
  }

  if (error) {
    return <p>{error}</p>;
  }

  return <RoleDashboard user={user} dashboardData={dashboardData} />;
}