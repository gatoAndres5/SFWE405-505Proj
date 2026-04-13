import { useEffect, useMemo, useState } from "react";
import RolePreviewPanel from "./RolePreviewPanel";
import RegistrationForm from "./RegistrationForm";
import RegistrationTable from "./RegistrationTable";

const API_BASE = "http://localhost:8080";

export default function RegistrationsPage() {
  const [registrations, setRegistrations] = useState([]);
  const [events, setEvents] = useState([]);
  const [participants, setParticipants] = useState([]);

  const [eventId, setEventId] = useState("");
  const [participantId, setParticipantId] = useState("");
  const [notes, setNotes] = useState("");
  const [selectedStatus, setSelectedStatus] = useState({});

  const [rolePreview, setRolePreview] = useState("");
  const [participantPreviewId, setParticipantPreviewId] = useState("");

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

  async function loadPageData() {
    setLoading(true);
    setError("");
    setMessage("");

    try {
      await Promise.all([
        fetchRegistrations(),
        fetchEvents(),
        fetchParticipants(),
      ]);
    } catch (err) {
      setError(err.message || "Failed to load page data.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadPageData();
  }, []);

  useEffect(() => {
    if (isParticipant && effectiveParticipantId) {
      setParticipantId(String(effectiveParticipantId));
    } else if (!canSelectParticipant) {
      setParticipantId("");
    }
  }, [isParticipant, effectiveParticipantId, canSelectParticipant]);

  useEffect(() => {
    if (rolePreview !== "PARTICIPANT") {
      setParticipantPreviewId("");
    }
  }, [rolePreview]);

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

  const selectedEvent = useMemo(() => {
    return events.find((event) => String(event.id) === String(eventId));
  }, [events, eventId]);

  const selectedParticipant = useMemo(() => {
    const lookupId = canSelectParticipant ? participantId : effectiveParticipantId;

    return participants.find(
      (participant) =>
        String(participant.participantId) === String(lookupId)
    );
  }, [participants, participantId, effectiveParticipantId, canSelectParticipant]);

  const visibleRegistrations = useMemo(() => {
    if (!isParticipant) return registrations;

    return registrations.filter(
      (registration) =>
        String(registration.participant?.participantId) ===
        String(effectiveParticipantId)
    );
  }, [registrations, isParticipant, effectiveParticipantId]);

  async function handleCreateRegistration(e) {
    e.preventDefault();
    setSubmitting(true);
    setError("");
    setMessage("");

    const effectiveCreateParticipantId = isParticipant
      ? effectiveParticipantId
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
    <div className="content-card">
      <h2>Registrations</h2>

      {message && <p style={{ color: "green" }}>{message}</p>}
      {error && <p style={{ color: "red" }}>{error}</p>}

      <RolePreviewPanel
        realIsAdmin={realIsAdmin}
        rolePreview={rolePreview}
        setRolePreview={setRolePreview}
        participantPreviewId={participantPreviewId}
        setParticipantPreviewId={setParticipantPreviewId}
        participants={participants}
        getParticipantLabel={getParticipantLabel}
      />

      {canCreate && (
        <RegistrationForm
          eventId={eventId}
          setEventId={setEventId}
          participantId={participantId}
          setParticipantId={setParticipantId}
          notes={notes}
          setNotes={setNotes}
          submitting={submitting}
          handleCreateRegistration={handleCreateRegistration}
          events={events}
          participants={participants}
          selectedEvent={selectedEvent}
          selectedParticipant={selectedParticipant}
          getEventLabel={getEventLabel}
          getParticipantLabel={getParticipantLabel}
          formatDateTime={formatDateTime}
          effectiveParticipantId={effectiveParticipantId}
          canSelectParticipant={canSelectParticipant}
          isParticipant={isParticipant}
        />
      )}

      <h3>Existing Registrations</h3>

      {loading ? (
        <p>Loading registrations...</p>
      ) : visibleRegistrations.length === 0 ? (
        <p>No registrations found.</p>
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
    </div>
  );
}