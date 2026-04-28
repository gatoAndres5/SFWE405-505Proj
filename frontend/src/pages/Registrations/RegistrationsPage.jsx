import { useEffect, useMemo, useState } from "react";
import RolePreviewPanel from "./RolePreviewPanel";
import RegistrationForm from "./RegistrationForm";
import RegistrationTable from "./RegistrationTable";
import "./Registrations.css";

const API_BASE = "http://localhost:8080";

export default function RegistrationsPage() {
  const [registrations, setRegistrations] = useState([]);
  const [events, setEvents] = useState([]);
  const [participants, setParticipants] = useState([]);
  const [users, setUsers] = useState([]);
  const [previewUserEvents, setPreviewUserEvents] = useState([]);

  const [eventId, setEventId] = useState("");
  const [participantId, setParticipantId] = useState("");
  const [notes, setNotes] = useState("");
  const [selectedStatus, setSelectedStatus] = useState({});

  const [rolePreview, setRolePreview] = useState("");
  const [participantPreviewId, setParticipantPreviewId] = useState("");
  const [organizerPreviewId, setOrganizerPreviewId] = useState("");
  const [staffPreviewId, setStaffPreviewId] = useState("");
  const [registrationFilter, setRegistrationFilter] = useState("");
  const [registrationSearch, setRegistrationSearch] = useState("");

  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const token = localStorage.getItem("token");

  const statusOptions = [
    "PENDING",
    "INVITED",
    "CONFIRMED",
    "WAITLISTED",
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

  const myParticipantId =
    jwtPayload?.participantId ??
    jwtPayload?.participant_id ??
    jwtPayload?.participantID ??
    null;

  const realIsAdmin = realRole === "ADMIN";

  const effectiveRole = realIsAdmin && rolePreview ? rolePreview : realRole;

  const effectiveParticipantId =
    realIsAdmin && effectiveRole === "PARTICIPANT"
      ? participantPreviewId || myParticipantId
      : myParticipantId;

  const isAdmin = effectiveRole === "ADMIN";
  const isOrganizer = effectiveRole === "ORGANIZER";
  const isStaff = effectiveRole === "STAFF";
  const isParticipant = effectiveRole === "PARTICIPANT";

  const canCreate = isAdmin || isOrganizer || isParticipant;
  const canSelectParticipant = isAdmin || isOrganizer;
  const canUpdateStatus = isAdmin || isOrganizer;
  const canCancel = isAdmin || isOrganizer;
  const canCheckInOut = isAdmin || isStaff;
  const canDelete = isAdmin;

  const organizers = users.filter((user) => user.role === "ORGANIZER");
  const staffUsers = users.filter((user) => user.role === "STAFF");

  function getOrganizerLabel(organizer) {
    return `${organizer.id} - ${organizer.username}`;
  }

  function getStaffLabel(staff) {
    return `${staff.id} - ${staff.username}`;
  }

  async function apiRequest(url, options = {}) {
    const res = await fetch(url, {
      ...options,
      headers: {
        ...(options.headers || {}),
        Authorization: token ? `Bearer ${token}` : "",
      },
    });

    if (!res.ok) {
      const text = await res.text();
      throw new Error(text || "Request failed.");
    }

    const contentType = res.headers.get("content-type");
    if (contentType && contentType.includes("application/json")) {
      return res.json();
    }

    return null;
  }

  async function fetchRegistrations() {
    const data = await apiRequest(`${API_BASE}/registrations`);
    setRegistrations(data || []);
  }

  async function fetchEvents() {
    const data = await apiRequest(`${API_BASE}/events`);
    setEvents(data || []);
  }

  async function fetchParticipants() {
    const data = await apiRequest(`${API_BASE}/participants`);
    setParticipants(data || []);
  }

  async function fetchUsers() {
    try {
      const res = await fetch(`${API_BASE}/users`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (!res.ok) throw new Error("Failed to fetch users");

      const data = await res.json();
      setUsers(data || []);
    } catch (err) {
      console.error(err);
      setUsers([]);
    }
  }

  async function fetchPreviewUserEvents(userId) {
    if (!userId) {
      setPreviewUserEvents([]);
      return;
    }

    try {
      const data = await apiRequest(`${API_BASE}/users/${userId}/events`);
      setPreviewUserEvents(data || []);
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
      const requests = [fetchRegistrations(), fetchEvents()];

      if (isAdmin || isOrganizer) {
        requests.push(fetchParticipants());
      }

      if (realIsAdmin) {
        requests.push(fetchUsers());
      }

      await Promise.all(requests);
    } catch (err) {
      setError(err.message || "Failed to load page data.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadPageData();
  }, []);

  const resolvedParticipantId = useMemo(() => {
    if (!isParticipant) return null;

    if (effectiveParticipantId) return effectiveParticipantId;

    const firstRegistrationWithParticipant = registrations.find(
      (registration) => registration.participant?.participantId != null
    );

    return firstRegistrationWithParticipant?.participant?.participantId ?? null;
  }, [isParticipant, effectiveParticipantId, registrations]);

  useEffect(() => {
    if (isParticipant && resolvedParticipantId) {
      setParticipantId(String(resolvedParticipantId));
    } else if (!canSelectParticipant) {
      setParticipantId("");
    }
  }, [isParticipant, resolvedParticipantId, canSelectParticipant]);

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
    if (!realIsAdmin) return;

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

  function getEventLabel(event) {
    return `${event.id} - ${event.name ?? event.title ?? "Unnamed Event"}`;
  }

  function getParticipantLabel(participant) {
    const fullName = `${participant.firstName ?? ""} ${participant.lastName ?? ""}`.trim();
    return `${participant.participantId} - ${fullName || "Unnamed Participant"}`;
  }

  function getEventName(event) {
    return event?.name ?? event?.title ?? "Unnamed Event";
  }

  function getParticipantName(participant) {
    const fullName = `${participant?.firstName ?? ""} ${participant?.lastName ?? ""}`.trim();
    return fullName || "Unnamed Participant";
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

  const previewUserEventIds = useMemo(() => {
    return previewUserEvents.map((event) => String(event.id));
  }, [previewUserEvents]);

  const visibleEvents = useMemo(() => {
    return events.filter((event) => {
      const isActive = event.status === "ACTIVE";

      if (!isActive) return false;

      if (
        realIsAdmin &&
        (effectiveRole === "ORGANIZER" || effectiveRole === "STAFF")
      ) {
        return previewUserEventIds.includes(String(event.id));
      }

      if (isOrganizer || isStaff) {
        return previewUserEventIds.includes(String(event.id));
      }

      if (isAdmin) return true;

      if (isParticipant) return true;

      return false;
    });
  }, [
    events,
    realIsAdmin,
    effectiveRole,
    previewUserEventIds,
    isAdmin,
    isOrganizer,
    isStaff,
    isParticipant,
  ]);

  const selectedEvent = useMemo(() => {
    return visibleEvents.find((event) => String(event.id) === String(eventId));
  }, [visibleEvents, eventId]);

  const visibleParticipants = useMemo(() => {
    return participants.filter((participant) => participant.active === true);
  }, [participants]);

  const selectedParticipant = useMemo(() => {
    const lookupId = canSelectParticipant ? participantId : resolvedParticipantId;

    return visibleParticipants.find(
      (participant) =>
        String(participant.participantId) === String(lookupId)
    );
  }, [
    visibleParticipants,
    participantId,
    resolvedParticipantId,
    canSelectParticipant,
  ]);

  const visibleRegistrations = useMemo(() => {
    let filtered = registrations;

    if (isParticipant) {
      filtered = filtered.filter(
        (registration) =>
          String(registration.participant?.participantId) ===
          String(resolvedParticipantId)
      );
    } else if (
      realIsAdmin &&
      (effectiveRole === "ORGANIZER" || effectiveRole === "STAFF")
    ) {
      filtered = filtered.filter((registration) =>
        previewUserEventIds.includes(String(registration.event?.id))
      );
    }

    if (registrationFilter) {
      filtered = filtered.filter(
        (registration) => registration.registrationStatus === registrationFilter
      );
    }

    if (registrationSearch.trim()) {
      const query = registrationSearch.trim().toLowerCase();

      filtered = filtered.filter((registration) => {
        const registrationId = String(registration.id ?? "").toLowerCase();
        const status = String(
          registration.registrationStatus ?? registration.status ?? ""
        ).toLowerCase();
        const notes = String(registration.notes ?? "").toLowerCase();

        const eventName = String(
          registration.event?.name ??
          registration.event?.title ??
          ""
        ).toLowerCase();

        const participantName = [
          registration.participant?.firstName ?? "",
          registration.participant?.lastName ?? "",
        ]
          .join(" ")
          .trim()
          .toLowerCase();

        const participantIdText = String(
          registration.participant?.participantId ?? ""
        ).toLowerCase();

        return (
          registrationId.includes(query) ||
          status.includes(query) ||
          notes.includes(query) ||
          eventName.includes(query) ||
          participantName.includes(query) ||
          participantIdText.includes(query)
        );
      });
    }

    return filtered;
  }, [
    registrations,
    isParticipant,
    resolvedParticipantId,
    realIsAdmin,
    effectiveRole,
    previewUserEventIds,
    registrationFilter,
    registrationSearch,
  ]);

  async function handleCreateRegistration(e) {
    e.preventDefault();
    setSubmitting(true);
    setError("");
    setMessage("");

    const effectiveCreateParticipantId = isParticipant
      ? resolvedParticipantId
      : participantId;

    if (!eventId) {
      setError("Please select an event.");
      setSubmitting(false);
      return;
    }

    if (!effectiveCreateParticipantId) {
      setError("No participant is available for this registration.");
      setSubmitting(false);
      return;
    }

    try {
      await apiRequest(
        `${API_BASE}/registrations?eventId=${encodeURIComponent(
          eventId
        )}&participantId=${encodeURIComponent(
          effectiveCreateParticipantId
        )}&notes=${encodeURIComponent(notes.trim())}`,
        {
          method: "POST",
        }
      );

      setMessage("Registration created successfully.");
      setEventId("");
      if (!isParticipant) {
        setParticipantId("");
      }
      setNotes("");
      await fetchRegistrations();
    } catch (err) {
      setError(err.message || "Failed to create registration.");
    } finally {
      setSubmitting(false);
    }
  }

  async function handleUpdateStatus(id) {
    const newStatus = selectedStatus[id];

    if (!newStatus) {
      setError("Please select a status first.");
      return;
    }

    setError("");
    setMessage("");

    try {
      await apiRequest(
        `${API_BASE}/registrations/${id}/status?newStatus=${encodeURIComponent(
          newStatus
        )}`,
        {
          method: "PUT",
        }
      );

      setMessage(`Registration ${id} updated to ${newStatus}.`);
      await fetchRegistrations();
    } catch (err) {
      setError(err.message || "Failed to update registration status.");
    }
  }

  async function handleCancelRegistration(id) {
    setError("");
    setMessage("");

    try {
      await apiRequest(`${API_BASE}/registrations/${id}/cancel`, {
        method: "PUT",
      });

      setMessage("Registration cancelled successfully.");
      await fetchRegistrations();
    } catch (err) {
      setError(err.message || "Failed to cancel registration.");
    }
  }

  async function handleCheckIn(id) {
    setError("");
    setMessage("");

    try {
      await apiRequest(`${API_BASE}/registrations/${id}/checkin`, {
        method: "PUT",
      });

      setMessage("Participant checked in successfully.");
      await fetchRegistrations();
    } catch (err) {
      setError(err.message || "Failed to check in participant.");
    }
  }

  async function handleCheckOut(id) {
    setError("");
    setMessage("");

    try {
      await apiRequest(`${API_BASE}/registrations/${id}/checkout`, {
        method: "PUT",
      });

      setMessage("Participant checked out successfully.");
      await fetchRegistrations();
    } catch (err) {
      setError(err.message || "Failed to check out participant.");
    }
  }

  async function handleDeleteRegistration(id) {
    setError("");
    setMessage("");

    try {
      await apiRequest(`${API_BASE}/registrations/${id}`, {
        method: "DELETE",
      });

      setMessage("Registration deleted successfully.");
      await fetchRegistrations();
    } catch (err) {
      setError(err.message || "Failed to delete registration.");
    }
  }

  function handleStatusChange(id, value) {
    setSelectedStatus((prev) => ({
      ...prev,
      [id]: value,
    }));
  }

  return (
    <div className="reg-page">
      {message && <div className="reg-message success">{message}</div>}
      {error && <div className="reg-message error">{error}</div>}

      {realIsAdmin && (
        <section className="reg-section">
          <h3 className="reg-section-title">Preview Page As</h3>
          <RolePreviewPanel
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
        <section className="reg-section">
          <h3 className="reg-section-title">Create Registration</h3>
          <RegistrationForm
            eventId={eventId}
            setEventId={setEventId}
            participantId={participantId}
            setParticipantId={setParticipantId}
            notes={notes}
            setNotes={setNotes}
            submitting={submitting}
            handleCreateRegistration={handleCreateRegistration}
            events={visibleEvents}
            participants={visibleParticipants}
            selectedEvent={selectedEvent}
            selectedParticipant={selectedParticipant}
            getEventLabel={getEventLabel}
            getParticipantLabel={getParticipantLabel}
            formatDateTime={formatDateTime}
            effectiveParticipantId={resolvedParticipantId}
            canSelectParticipant={canSelectParticipant}
            isParticipant={isParticipant}
          />
        </section>
      )}

      <section className="reg-section">
        <h3 className="reg-section-title">Existing Registrations</h3>

        <div className="reg-filter-bar">
          <label className="reg-label">Search registrations</label>
          <input
            className="reg-input"
            id="registrationSearch"
            type="text"
            value={registrationSearch}
            onChange={(e) => setRegistrationSearch(e.target.value)}
            placeholder="Search by ID, event, participant, status, or notes"
          />

          <label className="reg-label">Filter by status</label>
          <select
            className="reg-select"
            id="registrationFilter"
            value={registrationFilter}
            onChange={(e) => setRegistrationFilter(e.target.value)}
          >
            <option value="">All statuses</option>
            {statusOptions.map((status) => (
              <option key={status} value={status}>
                {status}
              </option>
            ))}
          </select>
        </div>

        {loading ? (
          <p className="reg-muted">Loading registrations...</p>
        ) : visibleRegistrations.length === 0 ? (
          <p className="reg-table-empty">No registrations found.</p>
        ) : (
          <RegistrationTable
            registrations={visibleRegistrations}
            canUpdateStatus={canUpdateStatus}
            canCancel={canCancel}
            canCheckInOut={canCheckInOut}
            canDelete={canDelete}
            selectedStatus={selectedStatus}
            handleStatusChange={handleStatusChange}
            handleUpdateStatus={handleUpdateStatus}
            handleCancelRegistration={handleCancelRegistration}
            handleCheckIn={handleCheckIn}
            handleCheckOut={handleCheckOut}
            handleDeleteRegistration={handleDeleteRegistration}
            getEventName={getEventName}
            getParticipantName={getParticipantName}
            formatDateTime={formatDateTime}
            statusOptions={statusOptions}
          />
        )}
      </section>
    </div>
  );
}