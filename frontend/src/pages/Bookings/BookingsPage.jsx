import { useEffect, useMemo, useState } from "react";
import axios from "axios";
import BookingForm from "./BookingForm";
import BookingTable from "./BookingTable";
import BookingRolePreviewPanel from "./BookingRolePreviewPanel";
import "./Bookings.css";

const API_BASE = "http://localhost:8080";

export default function BookingPage() {
  const [bookings, setBookings] = useState([]);
  const [events, setEvents] = useState([]);
  const [vendors, setVendors] = useState([]);
  const [participants, setParticipants] = useState([]);
  const [registrations, setRegistrations] = useState([]);
  const [users, setUsers] = useState([]);

  const [form, setForm] = useState({
    eventId: "",
    vendorId: "",
    serviceDescription: "",
    startDateTime: "",
    endDateTime: "",
  });

  const [rolePreview, setRolePreview] = useState("");
  const [participantPreviewId, setParticipantPreviewId] = useState("");
  const [organizerPreviewId, setOrganizerPreviewId] = useState("");
  const [staffPreviewId, setStaffPreviewId] = useState("");
  const [previewUserEvents, setPreviewUserEvents] = useState([]);

  const [bookingFilter, setBookingFilter] = useState("");
  const [bookingSearch, setBookingSearch] = useState("");

  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const token = localStorage.getItem("token");

  const bookingStatusOptions = [
    "REQUESTED",
    "CONFIRMED",
    "COMPLETED",
    "CANCELLED",
  ];

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

  const realIsAdmin = realRole === "ADMIN";
  const effectiveRole = realIsAdmin && rolePreview ? rolePreview : realRole;

  const isAdmin = effectiveRole === "ADMIN";
  const isOrganizer = effectiveRole === "ORGANIZER";
  const isStaff = effectiveRole === "STAFF";
  const isParticipant = effectiveRole === "PARTICIPANT";

  const canCreate = isAdmin || isOrganizer;
  const canManage = isAdmin || isOrganizer;

  const organizers = users.filter((user) => user.role === "ORGANIZER");
  const staffUsers = users.filter((user) => user.role === "STAFF");

  const axiosConfig = useMemo(
    () => ({
      headers: {
        Authorization: token ? `Bearer ${token}` : "",
      },
    }),
    [token]
  );

  function getOrganizerLabel(organizer) {
    return `${organizer.id} - ${organizer.username}`;
  }

  function getStaffLabel(staff) {
    return `${staff.id} - ${staff.username}`;
  }

  function getParticipantLabel(participant) {
    const fullName = `${participant.firstName ?? ""} ${participant.lastName ?? ""}`.trim();
    return `${participant.participantId} - ${fullName || "Unnamed Participant"}`;
  }

  function getEventLabel(event) {
    return `${event.id} - ${event.name ?? event.title ?? "Unnamed Event"}`;
  }

  function getVendorLabel(vendor) {
    return `${vendor.vendorId ?? vendor.id} - ${vendor.name ?? vendor.companyName ?? "Unnamed Vendor"}`;
  }

  function formatDateTime(value) {
    if (!value) return "N/A";

    return new Date(value).toLocaleString([], {
      year: "numeric",
      month: "short",
      day: "numeric",
      hour: "numeric",
      minute: "2-digit",
    });
  }

  async function fetchBookings() {
    const res = await axios.get(`${API_BASE}/bookings`, axiosConfig);
    setBookings(res.data || []);
  }

  async function fetchEvents() {
    const res = await axios.get(`${API_BASE}/events`, axiosConfig);
    setEvents(res.data || []);
  }

  async function fetchVendors() {
    const res = await axios.get(`${API_BASE}/vendors`, axiosConfig);
    setVendors(res.data || []);
  }

  async function fetchParticipants() {
    const res = await axios.get(`${API_BASE}/participants`, axiosConfig);
    setParticipants(res.data || []);
  }

  async function fetchRegistrations() {
    const res = await axios.get(`${API_BASE}/registrations`, axiosConfig);
    setRegistrations(res.data || []);
  }

  async function fetchUsers() {
    const res = await axios.get(`${API_BASE}/users`, axiosConfig);
    setUsers(res.data || []);
  }

  async function fetchPreviewUserEvents(userId) {
    if (!userId) {
      setPreviewUserEvents([]);
      return;
    }

    try {
      const res = await axios.get(`${API_BASE}/users/${userId}/events`, axiosConfig);
      setPreviewUserEvents(res.data || []);
    } catch (err) {
      console.error(err);
      setPreviewUserEvents([]);
    }
  }

  async function loadPageData() {
    setLoading(true);
    setError("");
    setMessage("");

    try {
      const requests = [fetchBookings(), fetchEvents()];

      if (canCreate) {
        requests.push(fetchVendors());
      }

      if (realIsAdmin) {
        requests.push(fetchParticipants(), fetchUsers(), fetchRegistrations());
      }

      await Promise.all(requests);
    } catch (err) {
      console.error(err);
      setError(
        err.response?.data?.message ||
          err.response?.data ||
          err.message ||
          "Failed to load booking data."
      );
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadPageData();
  }, [effectiveRole]);

  useEffect(() => {
    if (rolePreview !== "PARTICIPANT") {
      setParticipantPreviewId("");
    }
    if (rolePreview !== "ORGANIZER") {
      setOrganizerPreviewId("");
    }
    if (rolePreview !== "STAFF") {
      setStaffPreviewId("");
    }
  }, [rolePreview]);

  useEffect(() => {
    if (!realIsAdmin) {
      setPreviewUserEvents([]);
      return;
    }

    if (effectiveRole === "ORGANIZER" && organizerPreviewId) {
      fetchPreviewUserEvents(organizerPreviewId);
      return;
    }

    if (effectiveRole === "STAFF" && staffPreviewId) {
      fetchPreviewUserEvents(staffPreviewId);
      return;
    }

    setPreviewUserEvents([]);
  }, [realIsAdmin, effectiveRole, organizerPreviewId, staffPreviewId]);

  const previewParticipantEventIds = useMemo(() => {
    if (!realIsAdmin || effectiveRole !== "PARTICIPANT" || !participantPreviewId) {
      return [];
    }

    return registrations
      .filter(
        (registration) =>
          String(registration.participant?.participantId) ===
          String(participantPreviewId)
      )
      .map((registration) => String(registration.event?.id))
      .filter(Boolean);
  }, [realIsAdmin, effectiveRole, participantPreviewId, registrations]);

  const visibleEvents = useMemo(() => {
    return events.filter((event) => event.status === "ACTIVE");
  }, [events]);

  const selectedEvent = useMemo(() => {
    return visibleEvents.find(
      (event) => String(event.id) === String(form.eventId)
    );
  }, [visibleEvents, form.eventId]);

  const selectedVendor = useMemo(() => {
    return vendors.find(
      (vendor) =>
        String(vendor.vendorId ?? vendor.id) === String(form.vendorId)
    );
  }, [vendors, form.vendorId]);

  const visibleBookings = useMemo(() => {
    let filtered = bookings;

    if (
      realIsAdmin &&
      (effectiveRole === "ORGANIZER" || effectiveRole === "STAFF")
    ) {
      const previewEventIds = previewUserEvents.map((event) => String(event.id));

      filtered = filtered.filter((booking) =>
        previewEventIds.includes(String(booking.event?.id))
      );
    }

    if (realIsAdmin && effectiveRole === "PARTICIPANT" && participantPreviewId) {
      filtered = filtered.filter((booking) =>
        previewParticipantEventIds.includes(String(booking.event?.id))
      );
    }

    if (bookingFilter) {
      filtered = filtered.filter(
        (booking) => booking.bookingStatus === bookingFilter
      );
    }

    if (bookingSearch.trim()) {
      const query = bookingSearch.trim().toLowerCase();

      filtered = filtered.filter((booking) => {
        const bookingId = String(booking.bookingId ?? "").toLowerCase();
        const status = String(booking.bookingStatus ?? "").toLowerCase();
        const description = String(booking.serviceDescription ?? "").toLowerCase();

        const eventName = String(
          booking.event?.name ?? booking.event?.title ?? ""
        ).toLowerCase();

        const eventId = String(booking.event?.id ?? "").toLowerCase();

        const vendorName = String(
          booking.vendor?.name ?? booking.vendor?.companyName ?? ""
        ).toLowerCase();

        const vendorId = String(
          booking.vendor?.vendorId ?? booking.vendor?.id ?? ""
        ).toLowerCase();

        return (
          bookingId.includes(query) ||
          status.includes(query) ||
          description.includes(query) ||
          eventName.includes(query) ||
          eventId.includes(query) ||
          vendorName.includes(query) ||
          vendorId.includes(query)
        );
      });
    }

    return filtered;
  }, [
    bookings,
    realIsAdmin,
    effectiveRole,
    previewUserEvents,
    participantPreviewId,
    previewParticipantEventIds,
    bookingFilter,
    bookingSearch,
  ]);

  async function handleCreateBooking(e) {
    e.preventDefault();
    setSubmitting(true);
    setError("");
    setMessage("");

    try {
      await axios.post(`${API_BASE}/bookings`, null, {
        ...axiosConfig,
        params: {
          eventId: form.eventId,
          vendorId: form.vendorId,
          serviceDescription: form.serviceDescription.trim(),
          startDateTime: form.startDateTime,
          endDateTime: form.endDateTime,
        },
      });

      setMessage("Booking created successfully.");
      setForm({
        eventId: "",
        vendorId: "",
        serviceDescription: "",
        startDateTime: "",
        endDateTime: "",
      });
      await fetchBookings();
    } catch (err) {
      console.error(err);
      setError(
        err.response?.data?.message ||
          err.response?.data ||
          err.message ||
          "Failed to create booking."
      );
    } finally {
      setSubmitting(false);
    }
  }

  async function handleConfirmBooking(id) {
    setError("");
    setMessage("");

    try {
      await axios.put(`${API_BASE}/bookings/${id}/confirm`, null, axiosConfig);
      setMessage("Booking confirmed successfully.");
      await fetchBookings();
    } catch (err) {
      console.error(err);
      setError(
        err.response?.data?.message ||
          err.response?.data ||
          err.message ||
          "Failed to confirm booking."
      );
    }
  }

  async function handleCancelBooking(id) {
    setError("");
    setMessage("");

    try {
      await axios.put(`${API_BASE}/bookings/${id}/cancel`, null, axiosConfig);
      setMessage("Booking cancelled successfully.");
      await fetchBookings();
    } catch (err) {
      console.error(err);
      setError(
        err.response?.data?.message ||
          err.response?.data ||
          err.message ||
          "Failed to cancel booking."
      );
    }
  }

  async function handleCompleteBooking(id) {
    setError("");
    setMessage("");

    try {
      await axios.put(`${API_BASE}/bookings/${id}/complete`, null, axiosConfig);
      setMessage("Booking marked complete successfully.");
      await fetchBookings();
    } catch (err) {
      console.error(err);
      setError(
        err.response?.data?.message ||
          err.response?.data ||
          err.message ||
          "Failed to complete booking."
      );
    }
  }

  return (
    <div className="book-page">
      {message && <div className="book-message success">{message}</div>}
      {error && <div className="book-message error">{error}</div>}

      {realIsAdmin && (
        <section className="book-section">
          <h3 className="book-section-title">Preview Page As</h3>
          <BookingRolePreviewPanel
            realIsAdmin={realIsAdmin}
            rolePreview={rolePreview}
            setRolePreview={setRolePreview}
            participantPreviewId={participantPreviewId}
            setParticipantPreviewId={setParticipantPreviewId}
            participants={participants}
            getParticipantLabel={getParticipantLabel}
            organizerPreviewId={organizerPreviewId}
            setOrganizerPreviewId={setOrganizerPreviewId}
            organizers={organizers}
            getOrganizerLabel={getOrganizerLabel}
            staffPreviewId={staffPreviewId}
            setStaffPreviewId={setStaffPreviewId}
            staffUsers={staffUsers}
            getStaffLabel={getStaffLabel}
          />
        </section>
      )}

      {canCreate && (
        <section className="book-section">
          <h3 className="book-section-title">Create Booking</h3>
          <BookingForm
            form={form}
            setForm={setForm}
            handleCreateBooking={handleCreateBooking}
            submitting={submitting}
            events={visibleEvents}
            vendors={vendors}
            getEventLabel={getEventLabel}
            getVendorLabel={getVendorLabel}
            selectedEvent={selectedEvent}
            selectedVendor={selectedVendor}
            formatDateTime={formatDateTime}
          />
        </section>
      )}

      <section className="book-section">
        <h3 className="book-section-title">Existing Bookings</h3>

        <div className="book-filter-bar">
          <div className="book-filter-item">
            <label className="book-label" htmlFor="bookingSearch">
              Search bookings
            </label>
            <input
              id="bookingSearch"
              className="book-input"
              type="text"
              value={bookingSearch}
              onChange={(e) => setBookingSearch(e.target.value)}
              placeholder="Search by ID, event, vendor, status, or description"
            />
          </div>

          <div className="book-filter-item">
            <label className="book-label" htmlFor="bookingFilter">
              Filter by status
            </label>
            <select
              id="bookingFilter"
              className="book-select"
              value={bookingFilter}
              onChange={(e) => setBookingFilter(e.target.value)}
            >
              <option value="">All statuses</option>
              {bookingStatusOptions.map((status) => (
                <option key={status} value={status}>
                  {status}
                </option>
              ))}
            </select>
          </div>
        </div>

        {loading ? (
          <p className="book-table-empty">Loading bookings...</p>
        ) : visibleBookings.length === 0 ? (
          <p className="book-table-empty">No bookings found.</p>
        ) : (
          <BookingTable
            bookings={visibleBookings}
            canManage={canManage}
            handleConfirmBooking={handleConfirmBooking}
            handleCancelBooking={handleCancelBooking}
            handleCompleteBooking={handleCompleteBooking}
            formatDateTime={formatDateTime}
          />
        )}
      </section>
    </div>
  );
}